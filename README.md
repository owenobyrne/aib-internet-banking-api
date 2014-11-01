###AIB Online Banking API
I wanted to be able to access my bank account information from various sources, 
so I created this screenscraping API for AIB Online Banking. 

###Implementation
Screenscraping a banking website requires very accurate imitation of a real 
user using a browser. User-Agents are important. The sequence of links you navigate 
to get to certain information is important. Every link has a one-time-only CSRF token 
attached that must be used only when clicking that link. 

To achieve this with the AIB Online Banking site, my strategy is to always bring the 
browsing session back to a "home" page and store that page HTML in a datastore 
(currently [MapDB](http://www.mapdb.org/), for no particular reason. You can replace this with your own 
implementation of a `StorageService` if you prefer - there's a sample Cassandra implementation
included too). As each API call comes through, I navigate from this known state to the pages required and then 
back to the home page. This makes some API calls a bit slower than you would desire, but 
it's easier than trying to maintain a state engine that knows where you are and how to get 
the desired page from there all the time. 

### Authentication
Once you interact with the site initially it assigns a `JSESSIONID` cookie that must be 
maintained for the rest of the session. I store this in the datastore with the page contents, 
and create a completely independent UUID Session ID for the API to use. This keeps your actual 
session ID secret which is good for security reasons. 

The API Session ID is returned from the first API call `/v1/login/registration` and must be sent 
as a query string parameter to all subsequent API calls. E.g.

```
POST /v1/login/pac?sessionId=517215a0-8c6a-11e3-97ac-001422f0bc99
```

### API
All API endpoints accept Form Encoded data and return JSON.

#### Logging In - Step 1

| method | path                    |
|--------|-------------------------|
| POST   | /v1/login/registration  |

##### Payload

| name               	| description                             | example value                       |
|-----------------------|-----------------------------------------|-------------------------------------|
| REGISTRATION_NUMBER   | Your AIB Online Banking Reg Number 	  | 12345678                           	|	

##### Example

```bash
$ curl --data "REGISTRATION_NUMBER=12345678" $API_BASE/v1/login/registration
```

```json
{
    "sessionId": "517215a0-8c6a-11e3-97ac-001422f0bc99",
    "howmuch": "last four digits",
    "whatvalue": "primary AIB Visa Credit Card",
    "digit2": "3",
    "digit1": "2",
    "digit3": "5"
}
```

##### Properties

| name         		| description                                                                   | example value         |
|-------------------|-------------------------------------------------------------------------------|-----------------------|
| sessionId     	| The API Session ID for use in subsequent calls    							| {GUID}                |
| howmuch       	| The text to display or consume regarding how much of the `whatvalue` it wants.| "last four digits"    |
| whatvalue     	| Which of the Challenge questions you need to answer							| "home phone number"   |
| digit1, 2, and 3  | Which three digits of your PAC is it looking for.                             | 1,2,3,4 or 5     		|

#### Logging In - Step 2

| method | path                    |
|--------|-------------------------|
| POST   | /v1/login/pac 		   |

##### Payload

| name  	| description             							                | example value |
|-----------|-------------------------------------------------------------------|---------------|
| DIGITS  	| The Challenge digits requested, e.g. of your home phone number    | 1234          |
| PAC1,2,3  | The PAC digits requested. `PAC1` is the first digit requested specified in `digit1` from the previous response, not the first digit of your PAC.    | 0-9     |

##### Example

```bash
$ curl --data "PAC1=2&PAC2=3&PAC3=1&DIGITS=1234" $API_BASE/v1/login/pac?sessionId=517215a0-8c6a-11e3-97ac-001422f0bc99

```

```json
{
    "balances": 
        [{
            "id": 0,
            "name": "SAVINGS-123",
            "balance": 1085.42,
            "drcr": "",
            "transactions": null,
            "pending": false,
            "dr": false
        }]
}
```

##### Properties
The login step returns your list of accounts and their balances.

| name         	| description                               							    | example value |
|---------------|---------------------------------------------------------------------------|---------------|
| id			| The ID given to the account in the account list by AIB Online Banking. Doesn't always keep this ID in other situations. | 0 |
| name       	| Name of the account as in AIB Online Banking 								| "SAVINGS-123" |
| balance     	| BigDecimal balance of the account											| 100.00   		|
| drcr  		| String indication of the debit/credit status of the account. 				| "DR" 			|
| transactions 	| Can't remember what this is for right now, seems to be null all the time! | null 			|
| pending 		| Whether or not this account can have pending transactions. 				| true 			| 
| dr 			| Boolean indicating the debit or credit status of the balance. 			| true 			|

#### List accounts and balances
Gives the same response as Login - Step 2 above.

| method | path                     |
|--------|--------------------------|
| GET    | /v1/accounts/balances    |

##### Payload

None

##### Example

```bash
$ curl $API_BASE/v1/accounts/balances?sessionId=517215a0-8c6a-11e3-97ac-001422f0bc99

```

```json
{
    "balances": 
        [{
            "id": 0,
            "name": "SAVINGS-123",
            "balance": 1085.42,
            "drcr": "",
            "transactions": null,
            "pending": false,
            "dr": false
        }]
}
```

##### Properties
Returns your list of accounts and their balances.

| name         	| description                               							    | example value |
|---------------|---------------------------------------------------------------------------|---------------|
| id			| The ID given to the account in the account list by AIB Online Banking. Doesn't always keep this ID in other situations. | 0 |
| name       	| Name of the account as in AIB Online Banking 								| "SAVINGS-123" |
| balance     	| BigDecimal balance of the account											| 100.00   		|
| drcr  		| String indication of the debit/credit status of the account. 				| "DR" 			|
| transactions 	| Can't remember what this is for right now, seems to be null all the time! | null 			|
| pending 		| Whether or not this account can have pending transactions. 				| true 			| 
| dr 			| Boolean indicating the debit or credit status of the balance. 			| true 			|

#### List Transactions for account

| method | path                    					  |
|--------|--------------------------------------------|
| GET    | /v1/accounts/{accountName}/transactions    |

##### Payload

None

##### Example

```bash
$ curl $API_BASE/v1/accounts/SAVINGS-123/transactions?sessionId=517215a0-8c6a-11e3-97ac-001422f0bc99

```

```json
{
	"transactions": {
		"transactions": [
			{
				"transDate": 1379286000000,
				"narrative": "INTEREST CHARGED",
				"subNarrative": "(Incl. Surcharge- / Interest 0.00)",
				"amount": "195.86",
				"isDR": true,
				"subsequentBalance": ""
			}
		]
	}
}
```

##### Properties
Returns the list of transactions for account `accountName`. Not sure why it's wrapped in two `transactions` elements....

| name           	| description                               							    | example value			|
|-------------------|---------------------------------------------------------------------------|-----------------------|
| transDate			| Transaction date in milliseconds since the epoch. Rounded to nearest day. | 1379286000000 		|
| narrative       	| The primary narrative of the transaction.    						    	| "INTEREST CHARGED"	|
| subNarrative     	| All other sub-lines of information relating to this transaction 			| "(Incl Surcharge)		|
| amount  		    | The amount of the transaction.											| "29.23" 				|
| isDR 			    | Boolean indicating the debit or credit status of the transaction 			| true 					|
| subsequentBalance | The balance after this transaction										| "343.23 				|


#### List Pending Transactions for account
For accounts marked as pending in the response from `/v1/accounts/balances` you can retrieve all pending transactions 
using this endpoint.

| method | path                    					  |
|--------|--------------------------------------------|
| GET    | /v1/accounts/{accountName}/pending         |

##### Payload

None

##### Example

```bash
$ curl $API_BASE/v1/accounts/CURRENT-123/pending?sessionId=517215a0-8c6a-11e3-97ac-001422f0bc99
```

```json
[
	{
		"narrative": "MYMILKMAN", 
		"amount": "14.00",
		"account": "CURRENT-358",
		"isDR": true
	}
]
```

##### Properties
Returns the list of pending transactions for account `accountName`.

| name           	| description                               							    | example value			|
|-------------------|---------------------------------------------------------------------------|-----------------------|
| narrative       	| The primary narrative of the transaction.    						    	| "MYMILKMAN"			|
| account  		    | Rather pointless repeat of the account name :-/							| "CURRENT-123" 		|
| amount  		    | The amount of the transaction.											| "29.23" 				|
| isDR 			    | Boolean indicating the debit or credit status of the transaction 			| true 					|


#### Transfer between your own accounts
This is fairly experimental still - as there's a need for a digit of the PIN the first
time you transfer in a session, I need all the PIN digits. Alternatively I could 
turn this into a two-step API call like the login...

| method | path                    					  				 |
|--------|-----------------------------------------------------------|
| POST   | /v1/accounts/{accountNameFrom}/transferTo/{accountNameTo} |

##### Payload

| name  	| description             							                 | example value |
|-----------|--------------------------------------------------------------------|---------------|
| narrativeFrom  | The description to appear on the source account statement. Will be truncated to 12 chars.   | "From me"  |
| narrativeTo | The description to appear on the destination account statement. Will be truncated to 18 chars. | "To you"  |
| pinDigits | All your PIN Digits as a 5-digit number. This isn't the best idea! | 12345         |
| amount | The amount to transfer 												 | 50.00 		 |

##### Example

```bash
$ curl -d "narrativeFrom=From me&narrativeTo=to you&amount=20&pinDigits=12345" \
		$API_BASE/v1/accounts/CURRENT-123/transferTo/SAVINGS-234?sessionId=517215a0-8c6a-11e3-97ac-001422f0bc99
```

```json
{}
```

##### Properties

None

#### Logging Out

| method | path                    |
|--------|-------------------------|
| GET    | /v1/logout              |

##### Payload

None

##### Example

```bash
$ curl $API_BASE/v1/logout?sessionId=517215a0-8c6a-11e3-97ac-001422f0bc99
```

```json
{}
```

-------------------------
![githalytics.com alpha](https://cruel-carlota.pagodabox.com/86752651125cd250b51190c2d3295a88 "githalytics.com")


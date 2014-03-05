###AIB Online Banking API
I wanted to be able to access my bank account information from various sources, so I created this screenscraping API for AIB Online Banking. 

###Implementation
Screenscraping a banking website requires very accurate immitation of a real user using a browser. User-Agents are important. The sequence of links you navigate to get to certain information is important. Every link has a one-time-only CSRF token attached that must be used only when clicking that link. 

To achieve this with the AIB Online Banking site, my strategy is to always bring the browsing session back to a "home" page and store that page HTML in a datastore (currently Cassandra, for no reason other than I was experimenting with it). As each API call comes through, I navigate from this known state to the pages required and then back to the home page. This makes some API calls a bit slower than you would desire, but it's easier than trying to maintain a state engine that knows where you are and how to get the desire page from there all the time. 

### Authentication
Once you interact with the site initially it assigns a `JSESSIONID` cookie that must be maintained for the rest of the session. I store this in the datastore with the page contents, and create a completely independent UUID Session ID for the API to use. This keeps your actual session ID secret which is good for security reasons. 

The API Session ID is returned from the first API call `/v1/login/register` and must be sent as a query string parameter to all subsequent API calls. E.g.

```
POST /v1/login/pac?sessionId=517215a0-8c6a-11e3-97ac-001422f0bc99
```

### API
All API endpoints accept Form Encoded data and return JSON.

#### Logging In - Step 1

| method | path                    |
|--------|-------------------------|
| POST    | /v1/login/registration |

##### Payload

| name               | description                             | example value                          |
|--------------------|-----------------------------------------|----------------------------------------|
| REGISTRATION_NUMBER   | Your AIB Online Banking Reg Number    | 12345678                           |

##### Example

```bash
$ curl --data "REGISTRATION_NUMBER=12345678" $API_BASE/v1/login/register
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

| name         | description                                  | example value             |
|--------------|----------------------------------------------|---------------------------|
| sessionId     | The API Session ID for use in subsequent calls    | {GUID}                     |
| howmuch       | The text to display or consume regarding how much of the `whatvalue` it wants.                                    | "last four digits"                |
| whatvalue     | Which of the Challenge questions you need to answer| "home phone number"   |
| digit1, 2, and 3  | Which three digits of your PAC is it looking for.                                    | 1,2,3,4 or 5     |

#### Logging In - Step 2

| method | path                    |
|--------|-------------------------|
| POST    | /v1/login/pac |

##### Payload

| name               | description                             | example value                          |
|--------------------|-----------------------------------------|----------------------------------------|
| DIGITS  | The Challenge digits requested, e.g. of your home phone number    | 1234                           |
| PAC1,2,3  | The PAC digits requested. `PAC1` is the first digit requested specified in `digit1` from the previous response, not the first digit of your PAC.    | 0-9     |

##### Example

```bash
$ curl --data "PAC2=2&PAC3=3&PAC1=1&DIGITS=1234" $API_BASEv1/login/pac?sessionId=517215a0-8c6a-11e3-97ac-001422f0bc99

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

| name         | description                                  | example value             |
|--------------|----------------------------------------------|---------------------------|
| id     | The ID given to the account in the account list by AIB Online Banking. Doesn't always keep this ID in other situations. | 0 |
| name       | Name of the account as in AIB Online Banking | "SAVINGS-123" |
| balance     | BigDecimal balance of the account| 100.00   |
| drcr  | String indication of the debit/credit status of the account. | "DR" |                                      | 1,2,3,4 or 5     |
| transactions | Can't remember what this is for right now, seems to be null all the time! | null |
| pending | Whether or not this account can have pending transactions. | true | 
| dr | Boolean indicating the debit or credit status of the balance. | true |

#### More to Come.


# IP Geolocation API

## Structure

```
com/portfolio/ip_geolocation_api
├── domain/          # pure business logic, no dependencies
│   ├── model/       # entities, value objects
│   ├── service/     # domain services
│   └── exception/   # domain exceptions
├── application/     # use cases orchestration
│   ├── port/
│   │   ├── in/      # driving ports (exposed interfaces/cases)
│   │   └── out/     # driven ports (required interfaces)
│   ├── service/     # application service implementations
│   └── usecase/     # use case orchestration classes
├── infrastructure/  # adapters
│   ├── adapter/
│   │   ├── in/web/          # incoming: REST controllers
│   │   └── out/             # outgoing: persistence + rest clients
│   │       ├── persistence/
│   │       └── rest/
│   ├── config/      # beans & wiring
└── shared/          # cross-cutting shared code
```

## Use Cases

1. Create `IpAddr`
2. Read `IpAddr` by id
3. Update `IpAddr`
4. Delete `IpAddr`
5. List all `IpAddr`

## Core Entities

- `IpAddr` — represents an IP address as managed in the core domain layer.

```json
{
  "ip": "91.128.103.196",
  "location": {
    "continent_code": "EU",
    "continent_name": "Europe",
    "country_code": "SWE",
    "country_name": "Sweden",
    "country_capital": "Stockholm",
    "state_prov": "Stockholms län",
    "district": "Stockholm",
    "city": "Stockholm",
    "latitude": "59.40510",
    "longitude": "17.95510",
    "country_flag": "https://ipgeolocation.io/static/flags/se_64.png"
  }
}
```

## Endpoints

| Method/Path | Success | Errors |
|---|---|---|
| `POST /api/ip` | 201 + `IpAddr` | 400 blank/duplicate, 404 lookup failed, 409 exists |
| `GET /api/ip` | 200 + `List<IpAddr>` | — |
| `GET /api/ip/{ip}` | 200 + `IpAddr` | 404 |
| `PUT /api/ip/{ip}` | 200 + `IpAddr` | 400, 404 |
| `DELETE /api/ip/{ip}` | 204 | 400, 404 |

## Exceptions

All errors are returned as a structured `ApiError` body. The exception-to-response mapping is shown below.

| Exception | HTTP Status | Thrown When |
|---|---|---|
| `IpAddrNotFoundException` | 404 | The IP address is not stored and the external lookup found no match |
| `IpAddrAlreadyExistsException` | 409 | `POST /api/ip` with an IP already stored |
| `InvalidIpAddrDataException` | 400 | Blank/required IP input, invalid domain data |
| `GeolocationServiceUnavailableException` | 503 | Upstream geolocation API is down, timing out, or returned a 5xx |
| `GeolocationRateLimitedException` | 429 | Upstream geolocation API rate limit exceeded |
| `GeolocationAuthenticationException` | 500 | Upstream geolocation API rejected the API key (401/403) |
| `IpCachePersistenceException` | 500 | Local JSON cache file could not be read or written |
| `MethodArgumentNotValidException` / `ConstraintViolationException` | 400 | Bean validation failed on the request body |
| `HttpMessageNotReadableException` | 400 | Malformed or missing JSON request body |
| `MethodArgumentTypeMismatchException` | 400 | Path variable of the wrong type |
| `NoResourceFoundException` | 404 | Unknown path |
| `HttpRequestMethodNotSupportedException` | 405 | HTTP method not allowed for the path |
| Any unhandled `Exception` | 500 | Catch-all for unexpected failures |

## External API Response Structure

The following is the structure returned by the geolocation API (ipgeolocation.io):

```json
{
  "ip": "91.128.103.196",
  "location": {
    "continent_code": "EU",
    "continent_name": "Europe",
    "country_code2": "SE",
    "country_code3": "SWE",
    "country_name": "Sweden",
    "country_name_official": "Kingdom of Sweden",
    "country_capital": "Stockholm",
    "state_prov": "Stockholms län",
    "state_code": "SE-AB",
    "district": "Stockholm",
    "city": "Stockholm",
    "zipcode": "164 40",
    "latitude": "59.40510",
    "longitude": "17.95510",
    "is_eu": true,
    "country_flag": "https://ipgeolocation.io/static/flags/se_64.png",
    "geoname_id": "9972319",
    "country_emoji": "🇸🇪"
  },
  "country_metadata": {
    "calling_code": "+46",
    "tld": ".se",
    "languages": [
      "sv-SE",
      "se",
      "sma",
      "fi-SE"
    ]
  },
  "currency": {
    "code": "SEK",
    "name": "Swedish Krona",
    "symbol": "kr"
  },
  "asn": {
    "as_number": "AS1257",
    "organization": "Tele2 Sverige AB",
    "country": "SE"
  },
  "time_zone": {
    "name": "Europe/Stockholm",
    "offset": 1,
    "offset_with_dst": 2,
    "current_time": "2026-09-07 16:55:30.494+0200",
    "current_time_unix": 1788792930.494,
    "current_tz_abbreviation": "CEST",
    "current_tz_full_name": "Central European Summer Time",
    "standard_tz_abbreviation": "CET",
    "standard_tz_full_name": "Central European Standard Time",
    "is_dst": true,
    "dst_savings": 1,
    "dst_exists": true,
    "dst_tz_abbreviation": "CEST",
    "dst_tz_full_name": "Central European Summer Time",
    "dst_start": {
      "utc_time": "2026-03-29 TIME 01:00",
      "duration": "+1.00H",
      "gap": true,
      "date_time_after": "2026-03-29 TIME 03:00",
      "date_time_before": "2026-03-29 TIME 02:00",
      "overlap": false
    },
    "dst_end": {
      "utc_time": "2026-10-25 TIME 01:00",
      "duration": "-1.00H",
      "gap": false,
      "date_time_after": "2026-10-25 TIME 02:00",
      "date_time_before": "2026-10-25 TIME 03:00",
      "overlap": true
    }
  }
}
```
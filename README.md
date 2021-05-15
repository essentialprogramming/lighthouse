# lighthouse

Equivalence & Boundary Testing

Function Testing

Use case testing

Load testing

Penetration testing



Found issues:
---------------

  1.Create new Checkin entry. Does not work, even if correct input is provided. 
Error: Internal Server Error

  2.Create a visitor account. Delete the visitor account. Try to create a visitor account with the same email.
Expected : A visitor account is created.
Actual : Internal Server Error. 

  3.Update desk position fails most of the time. 

  4.Missing date validation when retrieveing checkins. 
      32 days for 31 days month is accepted. 
      31 days for 30 days month is accepted. 

  5.No reply attack protection. The api does not include a nonce or similar string to prevent a scenario where an attacker runs same request many times.
    See more : https://en.wikipedia.org/wiki/Replay_attack
               https://www.sitepoint.com/how-to-prevent-replay-attacks-on-your-website/
               https://crypto.stackexchange.com/questions/76875/why-does-a-nonce-prevent-a-replay-attack

  6.Cross-origin resource sharing (CORS) -Arbitrary Origin Trusted
     The application implements an HTML5 cross-origin resource sharing (CORS) policy for this request that allows access from any domain. 
     Allowing access from arbitrary domains means that those domains can perform two-way interaction with the application via this request. 
     Rather than using a wildcard or programmatically verifying supplied origins, use a whitelist of trusted domains.
     See more: 
         https://portswigger.net/web-security/cors/access-control-allow-origin



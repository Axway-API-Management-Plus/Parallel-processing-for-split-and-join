# Description
- Parallel processing of split messages
- Joint part of the process

## API Management Version Compatibilty
To be completed (message from the developer : "beware it won't work OOTB for 7.3.1 as HttpClient was removed for this version, dunno why")

## Install

```
To be completed
```

## Usage

```
First of all, if you wanted to use a polciy to do that, and invoke the policy in a loop, you've got to pass the current message in the following call : 
    InvocationEngine.invokeCircuit(circuit, context, msg);
and if you modify the current message between 2 calls to the policy, by setting content.body to be the new part you want to process it ends up in throwing an Exception.
  
For 
  After this all you've got to do is putting a Set Message filter and give ${joinedResponse} as the body.
```

## Bug and Caveats
```
```

## Changelog
```
```

## Contributing

Please read [Contributing.md] (/Contributing.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Team

![alt text][Axwaylogo] Axway Team

[Axwaylogo]: https://github.com/Axway-API-Management/Common/blob/master/img/AxwayLogoSmall.png  "Axway logo"

## License
Apache License 2.0 (refer to document [license] (/LICENSE))

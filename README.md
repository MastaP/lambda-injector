Lambda Injector is a Java Agent which enables to inject a body of a Runnable as a HTTP header and execute it before the HttpServletRequest is handled.

Add the following to the application server startup command line:

```
#!bash
-javaagent:"path/to/lambdainjector-agent.jar"
```

Add HTTP header 'LambdaInjectorRunnableBody=System.out.println("It Works!");' to your request, profit!

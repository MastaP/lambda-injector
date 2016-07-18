Lambda Injector is a Java Agent which enables to inject a body of a Runnable as a HTTP header and execute it before the HttpServletRequest is handled.

It might be useful in testing or debugging, when you don't want to recompile your project.

Add the following to the application server startup command line:

```
-javaagent:"path/to/lambdainjector-agent.jar"
```

Add HTTP header 'lambda_runnable_body=Thread.dumpStack();' to your request, profit!

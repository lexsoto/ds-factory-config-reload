Demonstrates issue where a Component Factory service is not unregistered/registered after configuration changes.

Per the specification (Section 112.5.5 of the "The OSGi Alliance, OSGi Enterprise, Release 6 July 2015")

"Once any of the listed conditions are no longer true, the component factory becomes unsatisfied and the Component Factory service must be unregistered."
 
The integration test's only method shows that after a configuration change this happens:

1- The Worker component 'shutdown' method is correctly called when the configuration changes.
2- The 'unbindFactory' is *not* called in _Booter_.  This is a bug in my opinion per the above cited paragraph.  
3- The reference _Worker_ held by _Booter_ is still valid after _Worker_ 'shutdown' method is called. This may be a bug, since the _Worker_ component has already been destroyed yet still lingering.


To reproduce:

*   mvn clean install  -DskipIT 
*   mvn verify
 
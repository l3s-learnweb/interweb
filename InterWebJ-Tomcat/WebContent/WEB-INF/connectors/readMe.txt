

///For running in debug mode with TED Connector:

The following two jars must be added to WEB-INF->connectors->ted->lib

xercesImpl-2.11.0.jar
xml-apis-1.4.01.jar

///For running in normal mode with TED Connector:

1) remove both of the files from the WEB-INF->connectors->ted->lib

if it still does not works fine then,

2)add the library xercesImpl-2.11.0.jar to WEB-INF->connectors->ted->lib

All the other libraries stay the same. 
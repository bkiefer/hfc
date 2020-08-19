
/Users/krieger/Desktop/Java/HFC/hfc/bin

java -server -cp .:../lib/trove-2.0.4.jar -Xms600m -Xmx1000m de/dfki/lt/tuplestore/ForwardChainer

java -server -cp .:../lib/trove-2.0.4.jar -Xms600m -Xmx1000m de/dfki/lt/hfc/Interactive

java -server -cp .:../lib/trove-2.0.4.jar:../lib/hfc.jar -Xms600m -Xmx1000m de/dfki/lt/hfc/cogx/CogXLTW ../resources/cogx.config ../resources/ltworld.jena.nt


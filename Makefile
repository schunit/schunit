build:
	@mvn -B --no-transfer-progress clean package javadoc:javadoc -P dist

release:
	@mvn release:prepare
	@mvn release:perform -Darguments="-Dmaven.deploy.skip=true"



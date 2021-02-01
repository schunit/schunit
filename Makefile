build:
	@mvn -B --no-transfer-progress clean package -P dist

javadoc:
	@mvn -B --no-transfer-progress javadoc:javadoc

release:
	@mvn release:prepare
	@mvn release:perform -Darguments="-Dmaven.deploy.skip=true"



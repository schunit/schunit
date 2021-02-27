build:
	@mvn -B --no-transfer-progress clean package javadoc:javadoc -P dist

docker:
	@docker build -t schunit/schunit:dev --target schunit .
	@docker build -t schunit/devcontainer:dev --target devcontainer .

release:
	@mvn release:prepare
	@mvn release:perform -Darguments="-Dmaven.deploy.skip=true"
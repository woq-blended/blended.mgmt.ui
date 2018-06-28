BUILD_DIR=.
THIRD_PTY_DIR=$(BUILD_DIR)/target/3rdparty

.PHONY: help # List of targets with descriptions
help:
	@grep '^.PHONY: .* #' Makefile | sed 's/\.PHONY: \(.*\) # \(.*\)/\1\t\2/' | expand -t20

.PHONY: all # Build the webapp and run all tests 
all: prepare test

.PHONY: webpack # Package the Webapplication for testing 
webpack: 
	sbt app/fastOptJS::webpack
	cp mgmt-app/index-dev.html mgmt-app/target/scala-2.12/scalajs-bundler/main

.PHONY: package # Package the web application into target/app
package: webpack
	mkdir -p mgmt-app/target/app/assets
	cp mgmt-app/index.html mgmt-app/target/app
	cp mgmt-app/target/scala-2.12/scalajs-bundler/main/node_modules/react/umd/react.production.min.js mgmt-app/target/app/assets
	cp mgmt-app/target/scala-2.12/scalajs-bundler/main/node_modules/react-dom/umd/react-dom.production.min.js mgmt-app/target/app/assets
	cp mgmt-app/target/scala-2.12/scalajs-bundler/main/mgmt-app-* mgmt-app/target/app/assets

.PHONY: test # Run all unit tests and UI tests
test:	package
	sbt test

.PHONY: clean # Run mvn clean
clean:
	rm -Rf target
	sbt clean

.PHONY: prepare # Prepare the UI build and build the dependencies
prepare: clean
	mkdir -p $(THIRD_PTY_DIR) 
	cd $(THIRD_PTY_DIR) && git clone https://github.com/woq-blended/react4s.git 
	cd $(THIRD_PTY_DIR)/react4s && sbt publishLocal

travis: all

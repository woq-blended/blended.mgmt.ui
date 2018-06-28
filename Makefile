BUILD_DIR=.
THIRD_PTY_DIR=$(BUILD_DIR)/target/3rdparty

.PHONY: help # List of targets with descriptions
help:
	@grep '^.PHONY: .* #' Makefile | sed 's/\.PHONY: \(.*\) # \(.*\)/\1\t\2/' | expand -t20

.PHONY: all # Build the webapp 
all: prepare test webpack

.PHONY: test # Run the tests
test: 
	sbt test

.PHONY: webpack # Package the Webapplication for testing 
webpack: 
	sbt app/fastOptJS::webpack
	cp mgmt-app/index-dev.html mgmt-app/target/scala-2.12/scalajs-bundler/main

.PHONY: clean # Run mvn clean
clean:
	rm -Rf $(THIRD_PTY_DIR) 
	sbt clean

.PHONY: prepare # Prepare the UI build and build the dependencies
prepare: clean
	mkdir -p $(THIRD_PTY_DIR) 
	cd $(THIRD_PTY_DIR) && git clone https://github.com/woq-blended/react4s.git 
	cd $(THIRD_PTY_DIR)/react4s && sbt publishLocal

travis: all

BUILD_DIR=.
THIRD_PTY_DIR=$(BUILD_DIR)/target/3rdparty

.PHONY: help # List of targets with descriptions
help:
	@grep '^.PHONY: .* #' Makefile | sed 's/\.PHONY: \(.*\) # \(.*\)/\1\t\2/' | expand -t20

.PHONY: all # Build the webapp and run all tests 
all: test package

.PHONY: webpack # Package the Webapplication for testing 
webpack: 
	sbt app/fastOptJS::webpack

.PHONY: package # Package the web application into target/app
package:
	sbt universal:packageBin

.PHONY: test # Run all unit tests and UI tests
test:
	sbt test

.PHONY: clean # Run mvn clean
clean:
	rm -Rf target
	sbt clean

.PHONY: gettext # extract translation string and update translation files
gettext:
	mkdir -p target/po
	find -type f -name "*.scala" | grep -v target > target/po/files.txt
	xgettext -f target/po/files.txt -L java -ktr -kmarktr --output target/po/messages.pot
	cd i18n && find . -name *.po -exec msgmerge -U {} ../target/po/messages.pot \; 

travis: all

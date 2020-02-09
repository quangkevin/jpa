GRADLE_HOME = /opt/gradle/gradle-6.0.1
GRADLE = $(GRADLE_HOME)/bin/gradle

clean ::
	$(GRADLE) clean

sample ::
	$(GRADLE) sample --stacktrace

jar ::
	$(GRADLE) jar

publish ::
	$(GRADLE) publishToMavenLocal

tasks ::
	$(GRADLE) tasks

build ::
	$(GRADLE) :compiler:sample




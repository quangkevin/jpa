GRADLE_HOME = /opt/gradle/gradle-6.0.1
GRADLE = $(GRADLE_HOME)/bin/gradle

clean ::
	$(exp) $(GRADLE) clean

sample ::
	$(exp) $(GRADLE) sample --stacktrace

jar ::
	$(exp) $(GRADLE) jar




#!/bin/sh
mvn clean install
MVN_VERSION=$(mvn -q \
    -Dexec.executable=echo \
    -Dexec.args='${project.version}' \
    --non-recursive \
    exec:exec)
echo 'test${MVN_VERSION}'
rm -r ~/Projects/A-DRZ/ADRZ-Repos/adrz-missionknowledge/common_lib/de/dfki/lt/hfc/hfc/*
rm -r ~/Projects/hfcrestapi/common_lib/de/dfki/lt/hfc/hfc/*
cp -r ~/.m2/repository/de/dfki/lt/hfc/hfc/${MVN_VERSION} ~/Projects/A-DRZ/ADRZ-Repos/adrz-missionknowledge/common_lib/de/dfki/lt/hfc/hfc/
cp -r ~/.m2/repository/de/dfki/lt/hfc/hfc/${MVN_VERSION} ~/Projects/hfcrestapi/common_lib/de/dfki/lt/hfc/

#!/usr/bin/env bash

# sudo apt-get --assume-yes install openjdk-11-jre-headless
sudo apt-get --assume-yes install openjdk-8-jdk
echo "export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64" > /etc/profile.d/javahome-vars.sh
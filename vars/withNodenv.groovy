#!/usr/bin/env groovy
import info.pedrocesar.utils

def call(version='6.14.4', method=null, cl) {
  def metarunner = 'nodenv'

  print "Setting up NodeJS version ${version}!"
  
  if (!fileExists("${JENKINS_HOME}/.nodenv/bin/nodenv")) {
    installNodenv()
  }

  if (!fileExists("${JENKINS_HOME}/.nodenv/versions/${version}/")) {
     print "Lets install Node ${version}!!!"
     utils.installVersion(metarunner, version)
  }

  withEnv(["PATH=${JENKINS_HOME}/.nodenv/shims:${JENKINS_HOME}/.nodenv/bin/:$PATH", "NODENV_SHELL=sh"]) {
    sh "nodenv rehash"
    sh "nodenv local ${version}"
    cl()
  }

  if (method == 'clean') {
    print "Removing NodeJS ${version}!!!"
    new utils().deleteVersion(metarunner, version)
  } 
  
}

def installNodenv() {
  print "Lets install Nodenv!!!"
  sh '''
     git clone https://github.com/nodenv/nodenv.git ${JENKINS_HOME}/.nodenv
     git clone https://github.com/nodenv/node-build.git ${JENKINS_HOME}/.nodenv/plugins/node-build
  '''

  dir ("${JENKINS_HOME}/.nodenv") {
    sh "src/configure --without-ssl && make -C src"     
  }
}

def purgeAll() {
  utils.purgeAllVersions(metarunner)
}

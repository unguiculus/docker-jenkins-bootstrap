freeStyleJob('__seed-job') {
    logRotator {
        numToKeep(30)
    }
    triggers {
        scm('* * * * *')
    }
    wrappers {
        timestamps()
    }
    scm {
        git {
            remote {
                url('https://github.com/unguiculus/job-dsl-sample.git')
                name('origin')
            }
            branch('origin/master')
            extensions {
                localBranch('master')
            }
        }
    }
    steps {
        dsl {
            additionalClasspath('src/main/groovy')
            external('jobs/jobs.groovy')
            ignoreExisting(false)
            lookupStrategy('JENKINS_ROOT')
            removeAction('DELETE')
            removeViewAction('DELETE')
        }
    }
}

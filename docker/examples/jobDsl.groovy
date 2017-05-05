multibranchPipelineJob('__seed-job') {
    branchSources {
        github {
            repoOwner('unguiculus')
            repository('job-dsl-sample')
            buildForkPRHead(false)
            buildForkPRMerge(true)
            buildOriginBranch(true)
            buildOriginBranchWithPR(false)
            buildOriginPRHead(false)
            buildOriginPRMerge(true)
        }
    }
    orphanedItemStrategy {
        discardOldItems {
            daysToKeep(0)
            numToKeep(0)
        }
    }
    triggers {
        periodic(1)
    }
}

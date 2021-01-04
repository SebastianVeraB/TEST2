

def blocks = []
deployCheckSubTitle = "Deploy Check"
jenkinsLogoURL = "https://user-images.githubusercontent.com/42625211/102991657-6dd22980-44f8-11eb-8bb6-b081a336253e.png"
gitHubLogoURL = "https://user-images.githubusercontent.com/42625211/102992734-893e3400-44fa-11eb-8884-b7c983348fa7.png"
pullRequestURL = pullRequest.url
jobURL = env.JOB_URL
userName = pullRequest.createdBy
pRName = pullRequest.title
def resolution

currentSubTitle = deployCheckSubTitle


def buildMessage(){
    buildCoreMessage()
    switch(resolution){
        case "success":
            addSuccessResolution()
        break
        case "component fail":
            addFailComponentRessolution()
        break
        case "apex fail":
            addApexFailResolution()
        break
    }
    addClosure()
    return blocks
}


def buildCoreMessage() {
    blocks =    [ 
                    [   
                        "type": "section",
                        "text": [   
                                    "type": "mrkdwn",
                                    "text": "Calypso Automation | *Check runs*"
                                ] 
                    ],
                    [
                        "type": "context",
                        "elements": [
                                        [
                                            "type": "mrkdwn",
					                        "text": ":avatar: \t $userName \n :pr-icon: \t $pRName \n :check-ls: \t $deployCheckSubTitle"
                                        ]
                                    ]
                    ],
                     [
                        "type": "header",
                        "text": [
                                    "type": "plain_text",
                                    "text": "Status",
                                    "emoji": true
                                ]
                    ],
                   
                ]
}

/*
                                      */

def setResolution(aResolution) {
    resolution = aResolution
}

def addSuccessResolution() {

    blocks.addAll(  [
                    [
                        "type": "context",
                        "elements": [
                                        [
                                            "type": "mrkdwn",
                                            "text": ":white_check_mark:  _Success_"
                                        ]
                                    ]
                    ]
                    
                ]
    )
}

def addClosure() {
    blocks.addAll(  [
                        [
                        "type": "divider"
                        ],
                        [
                            "type": "context",
                            "elements":[
                                        [
                                            "type": "image",
                                            "image_url": "$jenkinsLogoURL",
                                            "alt_text": "cute alfred"
                                        ],
                                        [
                                            "type": "mrkdwn",
                                            "text": "<$jobURL|Job>"
                                        ],
                                        [
                                            "type": "image",
                                            "image_url": "$gitHubLogoURL",
                                            "alt_text": "cute cat"
                                        ],
                                        [
                                            "type": "mrkdwn",
                                            "text": "<$pullRequestURL|Pull Request>"
                                        ]
                                    ]
                        ]
                    ]

                )
}

def addFailComponentRessolution() {
    blocks.addAll( [
                    [
                        "type": "context",
                        "elements": [
                                        [
                                            "type": "mrkdwn",
                                            "text": ":warning:   _Fail_\t *Errors found on Metadata components*"
                                        ]
                                    ]
                    ]
                ])
}

def addApexFailResolution() {
    blocks.addAll( [
                    [
                        "type": "context",
                        "elements": [
                                        [
                                            "type": "mrkdwn",
                                            "text": ":warning:   _Fail_\t *Apex test run failed*"
                                        ]
                                    ]
                    ]
                ])

}

return this
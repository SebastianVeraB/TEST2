

def blocks = []
def deployCheckSubTitle = "A new event at GitHub triggered Calypso Jenkins Automation Deploy Check pipeline"
def jenkinsLogoURL = "https://user-images.githubusercontent.com/42625211/102991657-6dd22980-44f8-11eb-8bb6-b081a336253e.png"
def gitHubLogoURL = "https://user-images.githubusercontent.com/42625211/102992734-893e3400-44fa-11eb-8884-b7c983348fa7.png"
def pullRequestURL = "test pull url"
def jobURL = "test job url"
def userName = "test Name url"
def pRName = "test pr name"
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
    return blocks
}


def buildCoreMessage() {
    blocks =    [ 
                    [   
                        "type": "section",
                        "text": [   
                                    "type": "plain_text",
                                    "text": "$currentSubTitle",
                                    "emoji": true 
                                ] 
                    ],
                    [
                        "type": "context",
                        "elements": [
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
                    ],
                    [
                        "type": "divider"
                    ],
                    [
                        "type": "header",
                        "text": [
                                    "type": "plain_text",
                                    "text": "Overview",
                                    "emoji": true
                                ]
                    ],
                    [
                        "type": "section",
                        "text": [
                                    "type": "mrkdwn",
                                    "text": "⎼⎼ _Pull Request details_ ⎼⎼\n \n\t•\t*Author*  \t\t$userName\n \t•\t*PR Name*\t\t\t$pRName \n \n ⎼⎼ _Status Check_ ⎼⎼"
                                ]
                    ]
                ]
}

def setResolution(aResolution) {
    resolution = aResolution
}

def addSuccessResolution() {

    blocks.add(  [
                    [
                        "type": "context",
                        "elements": [
                                        [
                                            "type": "mrkdwn",
                                            "text": ":white_check_mark:  _Success_"
                                        ]
                                    ]
                    ],
                    [
                        "type": "divider"
                    ]
                ]
    )
}

def addFailComponentRessolution() {
    blocks.add( [
                    [
                        "type": "context",
                        "elements": [
                                        [
                                            "type": "mrkdwn",
                                            "text": ":warning:   _Fail_\t *Errors found on Metadata components*"
                                        ]
                                    ]
                    ],
                    [
                        "type": "divider"
                    ],
                    [
                        "type": "header",
                        "text": [
                                    "type": "plain_text",
                                    "text": "Detail Log",
                                    "emoji": true
                                ]
                    ]
                ])
}

def addApexFailResolution() {
    blocks.add( [
                    [
                        "type": "context",
                        "elements": [
                                        [
                                            "type": "mrkdwn",
                                            "text": ":warning:   _Fail_\t *Apex test run failed*"
                                        ]
                                    ]
                    ],
                    [
                        "type": "divider"
                    ],
                    [
                        "type": "header",
                        "text": [
                                    "type": "plain_text",
                                    "text": "Detail Log",
                                    "emoji": true
                                ]
                    ]
                ])

}

return this
def SFDXResponse
def aResolution

def deploy(){
    return sh (script: "${toolbelt}/sfdx force:source:deploy -l RunLocalTests -p force-app/main/default/ --json > deployStatus.txt",  returnStatus: true) == 0
}


class sfdxOutcome{
    String resolution
    String detailLog
}

def getSFDXOutcome() {

    def output = readFile('output.txt').trim()
    //println output
    SFDXResponse = readJSON text: output

    def retrievedCoverageWarnings =  retrieveCoverageWarnings()
    def retrievedTestFailures = retrieveTestFailures()

    def summary = """SUMARY
    
    Metadata
    
    * Components with errors:  $SFDXResponse.result.numberComponentErrors
    * Components in total: $SFDXResponse.result.numberComponentsTotal 
    
    Apex run test
    
    * Failed test: $SFDXResponse.result.numberTestErrors 
    * Test total: $SFDXResponse.result.numberTestsTotal
    
    $retrievedCoverageWarnings"""

    def details =""" 
    """
    if(retrievedTestFailures) {
        details += retrievedTestFailures
    }
    else {
        details += retrieveComponentFailures()
    }
    def detail = summary + details
    println detail
    sh (script: 'echo ${detail} > detailLog.txt')
    return new sfdxOutcome(resolution: aResolution, detailLog: 'detailLog.txt')
   
}

def  retrieveCoverageWarnings() {
    def coverageToReturn = """"""

        if( hasCoverageWarnigns() ) {
            coverageToReturn+=   """Code coverage warnings"""
            if( hasMultipleCoverageWarnings() ) {
                def coverageList =  getCoverageWarnings()  
                coverageToReturn+=   """
                                        $coverageList 
                                        """
            }else {
                coverageToReturn+= """
                                        * $SFDXResponse.result.details.runTestResult.codeCoverageWarnings.message</li>
                                    """
            }
        } 
    return coverageToReturn    
}

def getCoverageWarnings() {
    def returnString = """"""
    SFDXResponse.result.details.runTestResult.codeCoverageWarnings.each { 
                            warning ->
                                               
                                if(warning.name in String) {
                                        returnString += """* $warning.name: $warning.message """
                                }
                                else {
                                        returnString += """* $warning.message """
                                }
    }
    return returnString
}

def hasCoverageWarnigns() {
    return SFDXResponse.result.details.runTestResult.containsKey('codeCoverageWarnings')
}

def hasMultipleCoverageWarnings() {
    return SFDXResponse.result.details.runTestResult.codeCoverageWarnings instanceof List
}

def hasTestFailures() {
    return SFDXResponse.result.details.runTestResult.numFailures > 0
}


def getTestFailures() {
    def failuresToReturn = """"""
    SFDXResponse.result.details.runTestResult.failures.each { 
        failure ->
            if(failure != null) {
                failuresToReturn += """ * Class: $failure.name 
                                        * Method: $failure.methodName
                                        * Error message: $failure.message 
                                        * Stacktrace: $failure.stackTrace"""
            }
    }
    return failuresToReturn
}

def retrieveTestFailures(){
    
    def failuresToReturn = """"""

    if( hasTestFailures() ) {
        aResolution = "apex fail"
        def testFailures = getTestFailures()
        failuresToReturn += """Apex test failures
                            
                            $testFailures
                            """
    }  
}

def retrieveComponentFailures(){

    def failuresToReturn = """"""

    if(hasComponentFailures()) {
        aResolution = "component fail"
        echo "there are component with failures"
        def componentsFailed = getComponentFailures()
        failuresToReturn += """Components failed

                                $componentsFailed 
                            """
    }
    return failuresToReturn
}

def getComponentFailures(){
    def failureComponentsToReturn = """"""
    SFDXResponse.result.details.componentFailures.each {
            componentFailure ->
            failureComponentsToReturn += """* $componentFailure.fullName
                                            
                                                > Component Type: $componentFailure.componentType
                                                > Problem Type: $componentFailure.problemType
                                                > Problem Description: $componentFailure.problem
                                            """
    }
    return failureComponentsToReturn
}

def hasComponentFailures(){
    return SFDXResponse.result.details.containsKey('componentFailures')
}

return this
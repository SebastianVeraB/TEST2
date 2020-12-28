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
    
    def details ="""⛈ SUMARY
    $retrievedCoverageWarnings"""

    if(retrievedTestFailures) {
        details += retrievedTestFailures
    }
    else {
        details += retrieveComponentFailures()
    }
    writeFile file: 'detailLog.txt', text: details
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
        failuresToReturn += """
                        * Failed test: $SFDXResponse.result.numberTestErrors 
                        * Test total: $SFDXResponse.result.numberTestsTotal"""
        def testFailures = getTestFailures()
        failuresToReturn += """Failures
                            
                            $testFailures
                            """
    }  
}

def retrieveComponentFailures(){

    def failuresToReturn

    if(hasComponentFailures()) {
        aResolution = "component fail"
        echo "there are component with failures"
        failuresToReturn = fillWith("▁")
        failuresToReturn += """\n\n• Components with errors:\t $SFDXResponse.result.numberComponentErrors\n• Components in total:\t $SFDXResponse.result.numberComponentsTotal\n
    """ + fillWith("▔")
        def componentsFailed = getComponentFailures()
        failuresToReturn += """\nFailures \n$componentsFailed"""
    }
    return failuresToReturn
}

def getComponentFailures(){
    def failureComponentsToReturn = """"""
    def count = 0
    SFDXResponse.result.details.componentFailures.each {
            componentFailure ->
            count ++
            failureComponentsToReturn += count + "-\n"
            failureComponentsToReturn += """$componentFailure.componentType / $componentFailure.fullName \n\n\t ⓘ $componentFailure.problemType\n\t“$componentFailure.problem”\n"""
            failureComponentsToReturn += fillWith("─")
    }
    return failureComponentsToReturn
}

def hasComponentFailures(){
    return SFDXResponse.result.details.containsKey('componentFailures')
}

def fillWith(token) {

    def devider = "" 
    for (i = 0; i < 40; i++) {
        devider += token
    }
    return devider
}

return this
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
    def retrievedComponentFailures = retrieveComponentFailures()

    def date = retrieveCompletedDate()

    def details ="""![logo3](https://user-images.githubusercontent.com/42625211/103368966-b7e27e80-4aa7-11eb-8b00-4fb86de8d174.png)
    &nbsp;\n# Summary\n##### ☁ QAMerge | $date"""

    if(retrievedCoverageWarnings) {
         details += retrievedCoverageWarnings
    }
    if(retrievedTestFailures) {
        details += retrievedTestFailures
    }
    else if(retrievedComponentFailures) {
        details += retrievedComponentFailures
    }
   
    writeFile file: 'detailLog.md', text: details
    return new sfdxOutcome(resolution: aResolution, detailLog: 'detailLog.md')
   
}

def  retrieveCoverageWarnings() {
    def coverageToReturn = ''

        if( hasCoverageWarnigns() ) {
            aResolution = "apex fail"
            coverageToReturn+=   """\n## Code coverage failure"""
            if( hasMultipleCoverageWarnings() ) {
                def coverageList =  getCoverageWarnings()  
                coverageToReturn+=   """\n\t$coverageList 
                                        """
            }else {
                coverageToReturn+= """\n###### ⓘ  Warning\n> $SFDXResponse.result.details.runTestResult.codeCoverageWarnings.message
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
                                        returnString += """\n###### ⓘ  Warning\n>  $warning.name: $warning.message """
                                }
                                else {
                                        returnString += """\n###### ⓘ  Warning\n> $warning.message """
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
    def count = 0
    def failuresToReturn = """"""
    SFDXResponse.result.details.runTestResult.failures.each { 
        failure ->
            if(failure != null) {
                count ++
                failuresToReturn += "\n"+ count + "."
                failuresToReturn += """**$failure.name** ⇨  $failure.methodName\n ###### ⓘ  Error Message\n> $failure.message\n###### ⓘ   Stacktrace\n> $failure.stackTrace"""
            }
    }
    echo "printing apex failures"
    println failuresToReturn
    return failuresToReturn
}

def retrieveTestFailures(){
    
    def failuresToReturn

    if( hasTestFailures() ) {
        echo "has test failures"
        aResolution = "apex fail"
        failuresToReturn += """\n* Failed test:\t$SFDXResponse.result.numberTestErrors\n* Total tests: $SFDXResponse.result.numberTestsTotal\n&nbsp;\n---"""
        def testFailures = getTestFailures()
        failuresToReturn += """\n## Failures\n##### *Class Name ⇨ Method Name\n$testFailures"""
    }  
}

def retrieveComponentFailures(){

    def failuresToReturn

    if(hasComponentFailures()) {
        aResolution = "component fail"
        echo "there are component with failures"
        failuresToReturn += """\n* Components with errors:\t $SFDXResponse.result.numberComponentErrors\n* Components in total:\t $SFDXResponse.result.numberComponentsTotal\n&nbsp;\n---"""
        def componentsFailed = getComponentFailures()
        failuresToReturn += """\n## Failures\n##### *Component Type ⇨ Component Name\n$componentsFailed"""
    }
    return failuresToReturn
}


def getComponentFailures(){
    def failureComponentsToReturn = """"""
    def count = 0
    .details.componentFailures.each {
            componentFailure ->
            count ++
            failureComponentsToReturn += "\n"+ count + "."
            failureComponentsToReturn += "**$componentFailure.componentType** ⇨ $componentFailure.fullName \n###### ⓘ  $componentFailure.problemType\n> $componentFailure.problem"
            
    }
    return failureComponentsToReturn
}

def hasComponentFailures(){
    return SFDXResponse.result.details.containsKey('componentFailures')
}

def retrieveCompletedDate() {
    String completedDateTime = SFDXResponse.result.completedDate
    
    String completedDate = completedDateTime.substring(0, 10 )
    completedDate = completedDate.substring( 5,10 ) + '-' + completedDate.substring( 0,4 ) 

    String completedTime = completedDateTime.substring( 11, 16 )
   
    return completedDate + " " + completedTime
}

return this
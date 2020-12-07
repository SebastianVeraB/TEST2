def SFDXResponse

def getSFDXOutcome() {

    def output = readFile('output.txt').trim()
    println output
    SFDXResponse = readJSON text: output

    def retrievedCoverageWarnings =  retrieveCoverageWarnings()
    def retrievedTestFailures = retrieveTestFailures()

    def summary = """
    <h3 id="summary-">Summary:</h3>
    <hr>
    <h4 id="metadata">Metadata</h4>
    <ul>
    <li>Components with errors:  $SFDXResponse.result.numberComponentErrors </li>
    <li>Components total: $SFDXResponse.result.numberComponentsTotal </li>
    </ul>
    <h4 id="apex-run-test">Apex run test</h4>
    <ul>
    <li>Failed test: $SFDXResponse.result.numberTestErrors </li>
    <li>Test total: $SFDXResponse.result.numberTestsTotal </li>
    </ul>
    $retrievedCoverageWarnings"""

    def details =""" """
    if(retrievedTestFailures) {
        details += retrievedTestFailures
    }
    else {
        details += retrieveComponentFailures()
    }
  if(details) {
        summary.toString().trim()
        details.toString().trim()
    }
    else {
        summary.toString().trim()
        details = ''
    }

    return [summary, details]    
   
}

def  retrieveCoverageWarnings() {
    def coverageToReturn = """"""

        if( hasCoverageWarnigns() ) {
            coverageToReturn+=   """<h4 id="code-coverage-warnings">Code coverage warnings</h4>"""
            if( hasMultipleCoverageWarnings() ) {
                def coverageList =  getCoverageWarnings()  
                coverageToReturn+=   """<ul> 
                                        $coverageList 
                                        </ul>"""
            }else {
                coverageToReturn+= """<ul>
                                        <li>$SFDXResponse.result.details.runTestResult.codeCoverageWarnings.message</li>
                                    </ul>"""
            }
        } 
    return coverageToReturn    
}

def getCoverageWarnings() {
    def returnString = """"""
    SFDXResponse.result.details.runTestResult.codeCoverageWarnings.each { 
                            warning ->
                                               
                                if(warning.name in String) {
                                        returnString += """<li>$warning.name: $warning.message </li>"""
                                }
                                else {
                                        returnString += """<li> $warning.message </li>"""
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
                failuresToReturn += """ <li>Class: $failure.name </li>
                                        <li>Method: $failure.methodName </li>
                                        <li>Error message: $failure.message </li>
                                        <li>Stacktrace: $failure.stackTrace </li>"""
            }
    }
    return failuresToReturn
}

def retrieveTestFailures(){
    
    def failuresToReturn = """"""

    if( hasTestFailures() ) {
        def testFailures = getTestFailures()
        failuresToReturn += """<h4 id="apex-test-failures">Apex test failures</h4>
                            <ul>
                                $testFailures
                            </ul>"""
    }  
}

def retrieveComponentFailures(){

    def failuresToReturn = """"""

    if(hasComponentFailures()) {
        echo "there are component failures"
        def componentsFailed = getComponentFailures()
        println componentsFailed
        failuresToReturn += """<h4 id="code-coverage-warnings">Components failed</h4>
                                <ul> $componentsFailed </ul>
                            """
    }
    return failuresToReturn
}

def getComponentFailures(){
    def failureComponentsToReturn = """"""
    SFDXResponse.result.details.componentFailures.each {
            componentFailure ->
            failureComponentsToReturn += """<li> $componentFailure.fullName</li>
                                            <ul>
                                                <li>Component Type: $componentFailure.componentType</li>
                                                <li>Problem Type: $componentFailure.problemType</li>
                                                <li>Problem Description: $componentFailure.problem</li>
                                            </ul>"""
    }
    return failureComponentsToReturn
}

def hasComponentFailures(){
    return SFDXResponse.result.details.containsKey('componentFailures')
}

return this
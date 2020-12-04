def getSFDXOutcome() {
    echo "Hello from bot!"
    return readFile('output.txt').trim()
} 

return this
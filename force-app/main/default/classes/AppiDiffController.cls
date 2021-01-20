public with sharing class AppiDiffController {
    
    @AuraEnabled
    public static void checkFiles( String root, String mirror)
    {
        ParserObjectFormat pof = new ParserObjectFormat();
        pof.setFileToParse(root);
        pof.parse();
        //serviceApiDiff sad = new serviceApiDiff(pof);
        //sad.checkObject(root, mirror);
        
    }


}
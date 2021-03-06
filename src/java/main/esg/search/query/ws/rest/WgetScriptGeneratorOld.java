package esg.search.query.ws.rest;

import java.util.List;

/**
 * Utility class to generate a wget script for a list of HTTP URLs.
 */
public class WgetScriptGeneratorOld {
    
    private final static String VERSION = "0.2";
    
    /**
     * Method to generate a wget script for download of selected files.
     * 
     * @param query : the query URL that was used to generate the list, inserted as a comment line in the script header
     * @param urls : the list of file URLs to download
     * @return
     */
    public static String createWgetScript(String query, final List<String> urls) {
        
        final StringBuilder wgetText = new StringBuilder();
        
        wgetText.append("#!/bin/bash\n\n");
        
        // add the header
        wgetText.append(headerString(query, VERSION));
        
        // add the environment variables
        wgetText.append(envVariablesString());
        
        // add the download function
        wgetText.append(downloadFunctionString(urls));

        wgetText.append("#\n# MAIN \n#\n");
        
        // add the main function
        wgetText.append(mainFunctionString());

        wgetText.append("exit 0\n");       
        
        return wgetText.toString();
        
    }
    
    private static String headerString(final String query, final String templateVersion) {
        String headerStr = "";
        
        headerStr += "##############################################################################\n";
        headerStr += "# ESG Federation download script\n";
        headerStr += "#\n";
        headerStr += "# Template version: " + templateVersion + "\n";
        headerStr += "# Query URL: "+query+"\n";
        headerStr += "#";
        headerStr += "##############################################################################\n\n\n";
        
        return headerStr;
    }
    
    private static String envVariablesString() {
        String envVariablesStr = "";
        
        envVariablesStr += "esgf_download_script_version=\"0.0.1\"\n";
        envVariablesStr += "esgf_cert=${esgf_cert:-\"${HOME}/.esg/credentials.pem\"}\n";
        envVariablesStr += "esgf_private=${esgf_private:-\"${HOME}/.esg/credentials.pem\"}\n\n";
       
        
        return envVariablesStr;
    }
    
    private static String downloadFunctionString(List<String> files) {
        String downloadFunctionStr = "";
        
        downloadFunctionStr += "esgf_download() {\n";
        
        for (String file : files) {
          downloadFunctionStr += "\t((debug || dry_run)) && " +
                                 "echo \"wget $@ --certificate ${esgf_cert} --private-key ${esgf_private} '" + file + "'\"\n";
          downloadFunctionStr += "\t((!dry_run)) && " +
                                 "wget \"$@\" --certificate ${esgf_cert} --private-key ${esgf_private} '" + file + "'\n";
          
        }
        
        downloadFunctionStr += "}\n";
             
        return downloadFunctionStr;
    }
    
    
    private static String mainFunctionString() {
        String mainFunctionStr = "";
        
        mainFunctionStr += "#Handle download script options\n";
        mainFunctionStr += "#Pass the rest directly to download command\n";
        mainFunctionStr += "main() {\n";
        
        mainFunctionStr += "\tlocal command_args=()\n";
        mainFunctionStr += "\twhile [ -n \"${1}\" ]; do\n";
        mainFunctionStr += "\t\tlocal unshift=0\n";
        mainFunctionStr += "\t\tcase ${1} in\n";
        mainFunctionStr += "\t\t\t--debug)\n \t\t\t\tdebug=1\n\t\t\t\t;;\n";
        mainFunctionStr += "\t\t\t--dry-run)\n \t\t\t\tdry_run=1\n\t\t\t\t;;\n";
        mainFunctionStr += "\t\t\t--certificate)\n \t\t\t\tshift\n\t\t\t\tesgf_cert=${1}\n\t\t\t\t;;\n";
        mainFunctionStr += "\t\t\t--private-key)\n \t\t\t\tshift\n\t\t\t\tesgf_private=${1}\n\t\t\t\t;;\n";
        mainFunctionStr += "\t\t\t--output-file)\n " + 
                           "\t\t\t\t#Because args passed are applied to each individual\n" + 
                           "\t\t\t\t#download we don't want to support this option in\n" + 
                           "\t\t\t\t#which case the output would be written over and over\n" + 
                           "\t\t\t\t#to the single file specified.  So this option is, in\n" + 
                           "\t\t\t\t#the context of this script, deamed unsupported.\n" + 
                           "\t\t\t\techo \"Unsupported option: --output-file\"\n" + 
                           "\t\t\t\texit 1\n\t\t\t\t;;\n"; 
        mainFunctionStr += "\t\t\t--help)\n " + 
                           "\t\t\t\techo \"ESGF dataset download script\"\n" + 
                           "\t\t\t\techo \"Version ${esgf_download_script_version}\"\n" + 
                           "\t\t\t\techo \n" + 
                           "\t\t\t\techo \" usage: $0 [--debug] [--dry-run] [--certificate <certfile>] [--private-key <private file>]\"\n" + 
                           "\t\t\t\techo \" (all other args are passed through args to wget command) \"\n" + 
                           "\t\t\t\techo \" (use --help --full to additionally see wget's help) \"\n" + 
                           "\t\t\t\techo \n" + 
                           "\t\t\t\tshift && [ \"$1\" = \"--full\" ] && echo \"$(wget --help)\"\n" + 
                           "\t\t\t\texit 0\n" + 
                           "\t\t\t\t;;\n";
        mainFunctionStr += "\t\t\t*)\n " + 
                           "\t\t\t\tcommand_args=(\"{command_args[@]} ${1}\") \n" + 
                           "\t\t\t\t;;\n";
        mainFunctionStr += "\t\tesac\n";
        mainFunctionStr += "\t\tshift\n";
        mainFunctionStr += "\tdone\n";
        mainFunctionStr += "\tesgf_download ${command_args[@]}\n";
        mainFunctionStr += "}\n\n";
        mainFunctionStr += "main $@\n";
                
        mainFunctionStr += "\n";
               
        return mainFunctionStr;
    }

}

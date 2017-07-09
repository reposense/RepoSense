package util;

import java.io.File;

/**
 * Created by matanghao1 on 10/7/17.
 */
public class FileUtil {

    public static String getRepoDirectory(String org, String repoName){
        return Constants.REPOS_ADDRESS + "/" + org + "/" + repoName+"/";
    }

    public static void deleteDirectory(String root)
    {

        File directory = new File(root);

        //make sure directory exists
        if(!directory.exists()){
            System.out.println("Directory does not exist.");
        }else{
            delete(directory);
        }
        System.out.println("Deletion Done");
    }

    private static void delete(File file) {

        if(file.isDirectory()){

            //directory is empty, then delete it
            if(file.list().length==0){

                file.delete();
            }else{

                //list all the directory contents
                String files[] = file.list();

                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);
                    //recursive delete
                    delete(fileDelete);
                }

                //check the directory again, if empty then delete it
                if(file.list().length==0){
                    file.delete();
                }
            }

        }else{
            //if file, then delete it
            file.delete();
        }
    }
}

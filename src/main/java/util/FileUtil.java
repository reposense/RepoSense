package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

/**
 * Created by matanghao1 on 10/7/17.
 */
public class FileUtil {

    public static void writeJSONFile(Object object, String path){
        Gson gson = new GsonBuilder()
                .setDateFormat(Constants.GITHUB_API_DATE_FORMAT).create();
        String result = gson.toJson(object);

        try {
            PrintWriter out = new PrintWriter(path);
            out.println(attachJsPrefix(result));
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

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

    public static void copyFiles(File src, File dest)
            throws IOException{

        if(src.isDirectory()){

            //if directory not exists, create it
            if(!dest.exists()){
                dest.mkdir();
                System.out.println("Directory copied from "
                        + src + "  to " + dest);
            }

            //list all the directory contents
            String files[] = src.list();

            for (String file : files) {
                //construct the src and dest file structure
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                //recursive copy
                copyFiles(srcFile,destFile);
            }

        }else{
            //if file, then copy it
            //Use bytes stream to support all file types
            copyFile(src,dest);
            //System.out.println("File copied from " + src + " to " + dest);
        }
    }

    public static void copyFile(File src, File dest){
        InputStream in = null;
        try {
            in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];

            int length;
            //copy the file content in bytes
            while ((length = in.read(buffer)) > 0){
                out.write(buffer, 0, length);
            }

            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String attachJsPrefix(String original){
        return "var resultJson = "+original;
    }

}

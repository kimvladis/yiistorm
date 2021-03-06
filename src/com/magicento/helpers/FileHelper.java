package com.magicento.helpers;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.ProjectScope;
import com.intellij.util.indexing.IOUtils;
import org.apache.tools.ant.util.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Enrique Piatti
 */
public class FileHelper
{

    @NotNull
    public static List<String> getParentDirectories(@NotNull File file)
    {
        List<String> directories = new ArrayList<String>();
        File directory = file.isDirectory() ? file : file.getParentFile();
        while(directory.isDirectory()){
            directories.add(0, directory.getName());
        }
        return directories;
    }

    @NotNull
    public static String[] getSubdirectories(@NotNull File file)
    {
        // FileUtils.getPathStack();
        // file.getAbsolutePath().replace("\\", "/").split("/");
        if(file.exists() && file.isDirectory()){
            return file.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return (new File(dir+"/"+name).isDirectory());
                    // return dir.isDirectory();
                }
            });
        }
        return new String[]{};
    }

    /**
     * returns all subdirectories as File class
     * @param folder
     * @return
     */
    @NotNull
    public static File[] getSubdirectoriesFiles(@NotNull File folder)
    {
        // FileUtils.getPathStack();
        // file.getAbsolutePath().replace("\\", "/").split("/");
        if(folder.exists() && folder.isDirectory()){
            return folder.listFiles( new FileFilter() {
                public boolean accept(File file) {
                    return file.isDirectory();
                }
            });
        }
        return new File[]{};
    }


    @NotNull public static String mergeFiles(@NotNull List<File> files)
    {
        String mergedContent = "";
        try {
            for(File file : files){
                mergedContent += FileUtil.loadFile(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mergedContent;

    }

    @NotNull public static List<PsiFile> findPsiFiles(Project project, String filePath)
    {
        List<PsiFile> files = new ArrayList<PsiFile>();
        int start = filePath.lastIndexOf('/');
        String fileName = filePath.substring(start == -1 ? 0 : start+1);

        PsiFile[] psiFiles = FilenameIndex.getFilesByName(project, fileName, ProjectScope.getProjectScope(project));

        if(psiFiles.length > 0){
            for(PsiFile psiFile : psiFiles){
                String fullPath = psiFile.getVirtualFile().getPath();
                if(fullPath.endsWith(filePath)){
                    files.add(psiFile);
                }
            }
        }
        return files;
    }


    @NotNull public static List<File> getAllFiles(File folder, boolean recursive)
    {
        List<File> listOfFiles = new ArrayList<File>();
        if(folder.exists() && folder.isDirectory()){
            File[] files = folder.listFiles();
            for(File file : files){
                if(file.isFile()){
                    listOfFiles.add(file);
                }
                else if(file.isDirectory() && recursive){
                    listOfFiles.addAll(getAllFiles(file, recursive));
                }
            }
        }
        return listOfFiles;
    }


    public static VirtualFile getVirtualFileFromFile(File file)
    {
        //virtualFile = VirtualFileManager.getInstance().findFileByUrl(file.getAbsolutePath());
        return LocalFileSystem.getInstance().findFileByIoFile(file);
        //virtualFile = VfsUtil.findFileByURL(VfsUtil.convertToURL(VfsUtil.pathToUrl(file.getAbsolutePath())))
    }

    public static File getFileFromVirtualFile(VirtualFile virtualFile)
    {
        return VfsUtil.virtualToIoFile(virtualFile);
    }

    public static PsiFile getPsiFileFromVirtualFile(VirtualFile virtualFile, Project project)
    {
        return PsiManager.getInstance(project).findFile(virtualFile);
    }

    public static PsiFile getPsiFileFromFile(File file, Project project)
    {
        VirtualFile vf = getVirtualFileFromFile(file);
        if(vf != null){
            return PsiManager.getInstance(project).findFile(vf);
        }
        return null;
    }


}

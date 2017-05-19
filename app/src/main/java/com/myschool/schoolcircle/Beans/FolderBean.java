package com.myschool.schoolcircle.Beans;

/**
 * Created by Mr.R on 2016/8/3.
 */
public class FolderBean {

    private String folderDir;//当前文件夹的路径
    private String firstImagePath;//图片路径
    private String folderName;//文件夹名称
    private int count;//图片数量

    public String getFolderDir() {
        return folderDir;
    }

    public void setFolderDir(String folderDir) {
        this.folderDir = folderDir;
        int lastIndexOf = this.folderDir.lastIndexOf("/");
        this.folderName = this.folderDir.substring(lastIndexOf+1);
    }

    public String getFirstImagePath() {
        return firstImagePath;
    }

    public void setFirstImagePath(String firstImagePath) {
        this.firstImagePath = firstImagePath;
    }

    public String getFolderName() {
        return folderName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

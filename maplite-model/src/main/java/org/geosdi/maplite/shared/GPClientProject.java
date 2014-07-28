/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geosdi.maplite.shared;

import java.io.Serializable;
import java.util.List;

/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
public class GPClientProject implements Serializable {

    private static final long serialVersionUID = 607645430816968379L;
    //
    private String name;
    private String image;
    private int numberOfElements;
    private int version;
    private Long id;
    private boolean shared;
    private String message;
//    private IGPUserSimpleDetail owner;
    private List<GPFolderClientInfo> rootFolders;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    public String getSharedLabel() {
        return shared ? "Shared" : "";
//        return shared ? LayerModuleConstants.INSTANCE.GPClientProject_sharedLabelText() : "";
    }

    public List<GPFolderClientInfo> getRootFolders() {
        return rootFolders;
    }

    public void setRootFolders(List<GPFolderClientInfo> rootFolders) {
        this.rootFolders = rootFolders;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "GPClientProject{" + "name=" + name + ", image=" + image + ", numberOfElements=" + numberOfElements + ", version=" + version + ", id=" + id + ", shared=" + shared + ", message=" + message + ", rootFolders=" + rootFolders + '}';
    }

}

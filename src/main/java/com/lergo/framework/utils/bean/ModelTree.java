package com.lergo.framework.utils.bean;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

//@SuppressWarnings("types")
//public class ModelTree<T extends ModelTree> {
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ModelTree<T> {

    private long id;
    private long parentId;
    private T local;
    private List<T> children;

    //	public abstract ModelTree<T> ModelTree(long id);
    public ModelTree(long id) {
        this.id = id;
    }

    //	public abstract ModelTree<T> ModelTree(long id, long parentId, T local);
    public ModelTree(long id, long parentId, T local) {
        this.id = id;
        this.parentId = parentId;
        this.setLocal(local);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public T getLocal() {
        return local;
    }

    public void setLocal(T local) {
        this.local = local;
    }

	public List<T> getChildren() {
		return children;
	}

	public void setChildren(List<T> children) {
		this.children = children;
	}
	
}

package com.lergo.framework.utils;

import com.lergo.framework.utils.bean.ModelTree;

import java.util.ArrayList;
import java.util.List;


/**
 * 树形结构工具类
 * @author lihexu
 *
 * @param <T>
 */

public class TreeTool<T extends ModelTree> {

	/**
	 * 根据原始列表组装树形结构
	 */
	@SuppressWarnings("unchecked")
	public T getTree(T root, List<T> mL) {
		List<T> c = getOneParentChildren(root,mL);//从前有座山
		if(!c.isEmpty()){
			root.setChildren(new ArrayList<T>());
			for (T webMenuTreeVo : c) {
				root.getChildren().add(getTree(webMenuTreeVo,mL));//讲的什么呢
			}
		}

		return root;
	}
	private List<T> getOneParentChildren(T root, List<T> mL) {
		List<T> r = new ArrayList<T>();//山里有个庙
		for (T menu : mL) {
			//if(root.getId().equals(menu.getParentid())){
			if(root.getId() == menu.getParentid()){
				r.add(menu);
			}
		}
		return r;//庙里有个老和尚讲故事
	}


	/*
	* 遍历树形结构
	* */
	@SuppressWarnings("unchecked")
	public void iteratorRunner (T tree,Iterator<T> iterator) {
		if(tree.getChildren()!=null) {
			for (Object childrenTreeObj : tree.getChildren()) {
				T childrenTree = (T)childrenTreeObj;
				iteratorRunner(childrenTree,iterator);//深度优先
				if(tree.getLocal() != null){
					if(iterator!=null){
						iterator.process(tree,childrenTree);
					}
				}
				//iteratorRunner(childrenTree,iterator);//广度优先
			}
		}
	}

	@FunctionalInterface
	public interface Iterator<T> {
		void process(T local,T children);
	}

}

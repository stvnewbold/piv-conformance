package gov.gsa.pivconformancegui;

import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

public class TestTree extends JTree {
	DatabaseTreeModel m_model;

	public TestTree() {
	}

	public TestTree(Object[] value) {
		super(value);
	}

	public TestTree(Vector<?> value) {
		super(value);
	}

	public TestTree(Hashtable<?, ?> value) {
		super(value);
	}

	public TestTree(TreeNode root) {
		super(root);
	}

	public TestTree(TreeModel newModel) {
		super(newModel);
		m_model = new DatabaseTreeModel(newModel);
	}

	public TestTree(TreeNode root, boolean asksAllowsChildren) {
		super(root, asksAllowsChildren);
	}
	
	public void RenderTreeFromModel() {
		m_model.doRender();
	}

}

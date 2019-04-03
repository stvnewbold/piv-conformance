package gov.gsa.pivconformancegui;

import javax.swing.text.BoxView;
import javax.swing.text.Element;

public class TestActionListener extends BoxView {

	public TestActionListener(Element elem, int axis) {
		super(elem, axis);
	}
	
	void doAddTestResult(TestEvent e) {
		StructuredEvent se = new StructuredEvent(e);
		Utils.AppendToBoxView(se, this);
	}


}

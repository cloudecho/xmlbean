# Usage of xmlbean #
```
package org.xmlbean.example;

import org.dom4j.DocumentException;
import org.xmlbean.XmlBeanHelper;
import org.xmlbean.annotation.ElementTag;

public class XmlBeanDemo {
	/**
	 * Usage of xmlbean
	 */
	public static void main(String[] args) throws InstantiationException,
			IllegalAccessException, DocumentException {
		System.out.println("xml:");
		String xml = "<root userId=\"1001\"><userName>Tom</userName><address><addressId>2001</addressId>"
				+ "<addressName>China</addressName>address-info</address>"
				+ "<linkphoe>13450006000</linkphoe><linkphoe>13450006001</linkphoe></root>";
		System.out.println(xml);

		// xml to bean
		System.out.println("\nxml to bean:");
		UserBean bean = XmlBeanHelper.getBean(xml, UserBean.class);
		System.out.println(bean.getUserid() + "  " + bean.getUsername() + "  "
				+ bean.getAddress().getAddressValue());

		// bean to xml
		System.out.println("\nbean to xml:");
		System.out.println(XmlBeanHelper.getDocument(bean, "root").asXML());

	}

	/**
	 * 约定：字段名小写．　<br>
	 * Bean对象结构与xml文档结构对应。<br>
	 * 字段与xml元素对应（由@ElementTag定义）
	 */
	public static class UserBean {
		@ElementTag(name = "userId", attribute = true)
		int userid;

		@ElementTag(name = "userName")
		String username;

		@ElementTag(name = "address")
		AddressBean address;

		@ElementTag(name = "linkphoe")
		String[] linkphoe;

		public int getUserid() {
			return userid;
		}

		public void setUserid(int userid) {
			this.userid = userid;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public AddressBean getAddress() {
			return address;
		}

		public void setAddress(AddressBean address) {
			this.address = address;
		}

		public String[] getLinkphoe() {
			return linkphoe;
		}

		public void setLinkphoe(String[] linkphoe) {
			this.linkphoe = linkphoe;
		}
	}

	public static class AddressBean {
		@ElementTag(name = "addressId")
		int addressid;

		@ElementTag(name = "addressName")
		String addressname;

		/**
		 * 元素文本字段命名规则： elementnameValue
		 */
		String addressValue;

		public int getAddressid() {
			return addressid;
		}

		public void setAddressid(int addressid) {
			this.addressid = addressid;
		}

		public String getAddressname() {
			return addressname;
		}

		public void setAddressname(String addressname) {
			this.addressname = addressname;
		}

		public String getAddressValue() {
			return addressValue;
		}

		public void setAddressValue(String addressValue) {
			this.addressValue = addressValue;
		}
	}
}
```
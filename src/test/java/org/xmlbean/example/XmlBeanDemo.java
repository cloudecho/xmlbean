package org.xmlbean.example;

import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.dom4j.DocumentException;
import org.xmlbean.XmlBeanHelper;
import org.xmlbean.annotation.ElementTag;

/**
 * Demo for xmlbean
 */
public class XmlBeanDemo {
	/**
	 * Usage of xmlbean
	 */
	public static void main(String[] args) throws InstantiationException,
			IllegalAccessException, DocumentException {
		System.out.println("[xml]");
		String xml = "<root userId=\"1001\"><userName>Tom</userName><address><addressId>2001</addressId>"
				+ "<addressName>China</addressName>address-info</address>"
				+ "<linkphone>13450006000</linkphone><linkphone>13450006001</linkphone></root>";
		System.out.println(xml);

		// xml to bean
		System.out.println("\n[xml to bean]");
		UserBean bean = XmlBeanHelper.getBean(xml, UserBean.class);
		System.out.println(bean);

		// bean to xml
		System.out.println("\n[bean to xml]");
		System.out.println(XmlBeanHelper.getDocument(bean, "root").asXML());

	}

	/**
	 * 约定：字段名遵循JAVA命名规范．　<br>
	 * Bean对象结构与xml文档结构对应。<br>
	 * 字段与xml元素对应（由@ElementTag定义）
	 */
	public static class UserBean {
		@ElementTag(name = "userId", attribute = true)
		int userId;

		@ElementTag(name = "userName")
		String userName;

		@ElementTag(name = "address")
		AddressBean address;

		@ElementTag(name = "linkphone")
		String[] linkphone;

		public int getUserId() {
			return userId;
		}

		public void setUserId(int userId) {
			this.userId = userId;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public AddressBean getAddress() {
			return address;
		}

		public void setAddress(AddressBean address) {
			this.address = address;
		}

		public String[] getLinkphone() {
			return linkphone;
		}

		public void setLinkphone(String[] linkphone) {
			this.linkphone = linkphone;
		}

		@Override
		public String toString() {
			return toBeanString(this);
		}

	}

	static String toBeanString(Object bean) {
		try {
			Map<?, ?> m = BeanUtils.describe(bean);
			m.remove("class");
			return m.toString();
		} catch (Exception e) {
			return bean.toString();
		}
	}

	public static class AddressBean {
		@ElementTag(name = "addressId")
		int addressId;

		@ElementTag(name = "addressName")
		String addressName;

		/**
		 * 元素文本字段命名规则： elementnameVal
		 */
		String addressVal;

		public int getAddressId() {
			return addressId;
		}

		public void setAddressId(int addressId) {
			this.addressId = addressId;
		}

		public String getAddressName() {
			return addressName;
		}

		public void setAddressName(String addressName) {
			this.addressName = addressName;
		}

		public String getAddressVal() {
			return addressVal;
		}

		public void setAddressVal(String addressVal) {
			this.addressVal = addressVal;
		}

		@Override
		public String toString() {
			return toBeanString(this);
		}

	}
}

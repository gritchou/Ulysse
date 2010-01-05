package org.qualipso.factory.jabuti.persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class Dao {
	Connection conn;
	
	public Dao(Properties prop) {
		conn = JabutiConnection.getConnection(prop);
	}
	
	public void insert(JabutiServiceProject jsproject) {
        try {
            Statement st = conn.createStatement();
            String query = "INSERT INTO project(id, name, state) VALUES ";
            query += "('" + jsproject.getProjid() + "', '" + jsproject.getName() + "', " + jsproject.getState() + ")";
            System.out.println("DAO insert query: " + query);
            st.executeUpdate(query);
            st.close();            
        } 
        catch (SQLException ex) {
        	System.out.println("DAO insert SQL Exception.");
            ex.printStackTrace();
        }		
	}
	
	public void update(JabutiServiceProject jsproject) {
        try {
            Statement st = conn.createStatement();
            String query = "UPDATE project SET name='" + jsproject.getName() + "', testsuite='" + jsproject.getTestsuiteclassname() + "', selectedclasses='" + jsproject.getSelectedclasses() + "', ignoredclasses='" + jsproject.getIgnoredclasses() + "', state=" + jsproject.getState() + " WHERE id='" + jsproject.getProjid() + "'";
            st.executeUpdate(query);
            st.close();
        } 
        catch (Exception ex) {
            ex.printStackTrace();
        } 		
	}

	public boolean delete(String projid)
	{
        try {
            Statement st = conn.createStatement();
            String query = "DELETE FROM project WHERE id='" + projid + "'";
            st.executeUpdate(query);
            st.close(); 
            return true;
        } 
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }		
		
	}

	public JabutiServiceProject get(String projid)
	{
       try {
    	   JabutiServiceProject jsp = new JabutiServiceProject();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM project WHERE id='"+ projid +"'");
            if(rs.next())
            {
            	jsp.setProjid(rs.getString("id"));
            	jsp.setName(rs.getString("name"));
            	jsp.setTestsuiteclassname(genNull(rs.getString("testsuite")));
            	jsp.setSelectedclasses(genNull(rs.getString("selectedclasses")));
            	jsp.setIgnoredclasses(genNull(rs.getString("ignoredclasses")));
            	jsp.setState(rs.getInt("state"));
            }
            rs.close();
            st.close();
            return jsp;
        } 
        catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return null;	
    }
	
	private String genNull(String s)
	{
		if(s == null)
			return null;
		if(s.equals("null"))
			return null;
		
		return s;
	}
	
	public static void main(String args[])
	{
		/*System.out.println("test dao");
		Dao dao = new Dao();
		String projid = String.valueOf(System.nanoTime());
		
		JabutiServiceProject p = new JabutiServiceProject();
		p.setProjid(projid);
		p.setName("proj1");
		p.setTestsuiteclassname("package.package.TestSuite");
		p.setSelectedclasses("[clcclcl, clclc, lcclclc, clcclclc]");
		p.setIgnoredclasses("[clcclcl, clclc, lcclclc, clcclclc]");
		p.setState(1);
		
		dao.insert(p);

		p.setName("proj12");
		p.setTestsuiteclassname("package.package.TestSuite2");
		p.setSelectedclasses("[clcclcl, clclc, lcclclc, clcclclc]2");
		p.setIgnoredclasses("[clcclcl, clclc, lcclclc, clcclclc]2");
		p.setState(2);
		
		dao.update(p);
		
		//dao.delete("27349791314156");
		
		JabutiServiceProject jsp = dao.get(projid);
		System.out.println(jsp.getProjid());
		System.out.println(jsp.getName());
		System.out.println(jsp.getTestsuiteclassname());
		System.out.println(jsp.getSelectedclasses());
		System.out.println(jsp.getIgnoredclasses());
		System.out.println(jsp.getState());
		
		JabutiServiceProject jsp = dao.get("8467040828338");
		System.out.println(jsp.getSelectedclasses());
		if(jsp.getSelectedclasses() == null)
			System.out.println("null");
		else
			System.out.println("not null");*/
	}
	
}

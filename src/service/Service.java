package service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import beans.Profit;
import jdbc.JdbcConn;

public class Service {
	private Connection dbconnection;
	private Statement st,st1,st2;
	private ResultSet rs,rs1,rs2;
	private String sql;
	private List list;
	private Profit pf;
	
	public List getProfit() {
		list = new ArrayList();
		dbconnection = JdbcConn.getConnection();
		try {
			st = (Statement)dbconnection.createStatement();
			st1 = (Statement)dbconnection.createStatement();
			st2 = (Statement)dbconnection.createStatement();
			sql = "select g.goods_name goodsName,g.selling_price selling,g.cost_price costPrice,g.goods_id goodsId "
					+ "from goodslist g, trading_information t "
					+ "where t.trading_goods_id=g.goods_id "
					+ "group by g.goods_name,g.selling_price,g.cost_price,g.goods_id";
			rs = st.executeQuery(sql);
			int temp;
			while(rs.next()) {
				pf = new Profit();
				pf.setGoodsName(rs.getString("goodsName"));
				pf.setSellingPrice(rs.getInt("selling"));
				pf.setCostPrice(rs.getInt("costPrice"));
				pf.setGoodsId(rs.getInt("goodsId"));

				temp = 0;
				temp = pf.getSellingPrice() - pf.getCostPrice();
				
				sql = "select sum(t.trading_number) sumNum "
						+ "from trading_information t "
						+ "where t.trading_goods_id = " + pf.getGoodsId();
				rs1 = st1.executeQuery(sql);
				while(rs1.next()) {
					pf.setTradingNum(rs1.getInt("sumNum"));
					
				}
				
				pf.setPorfit(temp*pf.getTradingNum());
				sql = "select count(t.trading_id) times "
						+ "from trading_information t "
						+ "where t.trading_goods_id = " + pf.getGoodsId();
				rs2 = st2.executeQuery(sql);
				while(rs2.next()) {
					pf.setTimes(rs2.getInt("times"));
				}
				list.add(pf);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
}

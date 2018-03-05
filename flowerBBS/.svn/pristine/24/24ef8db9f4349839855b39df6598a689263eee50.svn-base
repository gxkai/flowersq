package com.flower.FrontCtrl;


import java.awt.print.Printable;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.swing.text.AbstractDocument.Content;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.flower.util.QianNiuUpload;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.AccessTokenApi;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.TemplateData;
import com.jfinal.weixin.sdk.api.TemplateMsgApi;
import com.jfinal.weixin.sdk.utils.HttpUtils;
import com.util.Sign;
import com.util.XMLParser;


import com.flower.util.Constant;
import com.flower.util.GiveTip;
import com.controller.WeixinApiCtrl;
import com.flower.Dao.IndexDao;
import com.flower.interceptor.FrontInterceptor;
import com.flower.util.WithdrawCash;

@Before(FrontInterceptor.class)
public class FrontIndexCtrl extends Controller {

	public void index_share(){
		render("index_share.html");
	}
	public void index3() throws InterruptedException, ExecutionException {
		 Record account = getSessionAttr("account");
		 if(account!=null){
				//获取话题
			    CompletableFuture<List<Record>> completableFuture1=
	                 CompletableFuture.supplyAsync(() -> {
	                	 List<Record> theme = Db.find("SELECT id,title,focusNum FROM f_theme WHERE isValid = 1 ORDER BY orderId");
	                	 for(int i=0;i< theme.size();i++){
	     					List<Record> imglist =  Db.find("SELECT headimg FROM f_myfocus as a LEFT JOIN f_account as b ON a.aid = b.id WHERE a.themeId = ? LIMIT 4",theme.get(i).getInt("id"));
	     					int j = 1;
	     					if (imglist != null) {
	     						//添加图片
	     						for (Record img : imglist) {
	     							theme.get(i).set("img0"+j+"", img.getStr("headimg"));
	     							j++;
	     						}
	     					}
	     				}
	                     return theme;
	                 });
				//我的关注
				//获取用户id
				
				int aid=account.getInt("id");
				 CompletableFuture<List<Record>> completableFuture2=
		                 CompletableFuture.supplyAsync(() -> {
		                		List<Record> myfocus = IndexDao.getMyfocus(aid);
		                	 return myfocus;
		                 });
			
				
				
				//所有精品贴
				CompletableFuture<List<Record>> completableFuture3=
		                 CompletableFuture.supplyAsync(() -> {
		     				List<Record> alljx = IndexDao.getJx(account.getInt("id"));
		                	 return alljx;
		                 });
				
				
				//所有帖子 抓了前20 
				CompletableFuture<List<Record>> completableFuture4=
		                 CompletableFuture.supplyAsync(() -> {
		     				List<Record> allpost = IndexDao.getAllpost(account.getInt("id"));
		                	 return allpost;
		                 });
				
				
				CompletableFuture<Record> completableFuture5=
		                 CompletableFuture.supplyAsync(() -> {
		     				Record core=Db.findFirst("select count(1) 'count' from f_myIntegral where aid=?",aid);			
		                	 return core;
		                 });
			    CompletableFuture<Void> allResult=CompletableFuture.allOf(completableFuture1,completableFuture2,completableFuture3,completableFuture4,completableFuture5);

		        //阻塞等待所有任务执行完成
		        allResult.join();
				//获取每个话题前4位的头像
				setAttr("themelist", completableFuture1.get());
		        setAttr("myfocuslist", completableFuture2.get());
		        setAttr("alljx", completableFuture3.get());
		        setAttr("allpost", completableFuture4.get());
				setAttr("core", completableFuture5.get());
				render("index.html");
			}
	}
	/**
	 * 主页
	 */
	public void index(){
		String rootPath=getClass().getResource("/").getFile().toString();//查找根目录
		System.out.println(rootPath);

		Record account = getSessionAttr("account");
		
		if(account!=null){
			//获取话题
			List<Record> theme = Db.find("SELECT id,title,focusNum,des,imgurl FROM f_theme WHERE isValid = 1 ORDER BY orderId");
			
			setAttr("themelist", theme);
			
			//我的关注
			//获取用户id
			
			int aid=account.getInt("id");
			List<Record> myfocus = IndexDao.getMyfocus(aid);
			setAttr("myfocuslist", myfocus);
			
			//所有精品贴
			List<Record> alljx = IndexDao.getJx(account.getInt("id"));
			setAttr("alljx", alljx);
			
			//所有帖子 抓了前20 
			List<Record> allpost = IndexDao.getAllpost(account.getInt("id"));
			setAttr("allpost", allpost);
			
			List<Record> comlist=IndexDao.getCom();//抓出所有帖子的前两条评论
			setAttr("comlist", comlist);
			
			System.out.println(comlist);
			
			String headimg=account.getStr("headimg");	
			String nick=account.getStr("nick");	
			setAttr("headimg", headimg);
			setAttr("nick", nick);
			render("index.html");
		}
	}
	
	
	
	/**
	 * 社区规范
	 */
	public void rule() {
		render("rule.html");
	}
	
	/**
	 * 动态加载更多
	 * @author lmy
	 * @date 2018年1月30日
	 */
	public void load() {
		Record account = getSessionAttr("account");
		int aid=account.getInt("id");
		int num = getParaToInt("num");
		String parentId = getPara("parentId");
		
		List<Record> searchList =new ArrayList<>();
		if(parentId.equals("page_1")){
			//最新
			searchList = Db.find("SELECT a.id,a.aid,a.content,a.img01,a.img02,a.img03,a.img04,a.img05,a.img06,a.img07,a.img08,a.img09,"
					+ " a.ctime,a.fingerNum,a.tipsNum,a.commentNum,a.type,a.themeId,a.isVaild,b.headimg,b.nick,c.title,"
					+ " case b.grade WHEN 1 then '嫩绿新叶' WHEN 2 then '可爱小苗' WHEN 3 then '亭亭玉立' "
					+ " WHEN 4 then '娇羞花蕾' WHEN 5 then '含苞待放' WHEN 6 then '华丽绽放' end 'grade',"
					+ " case when d.id is null then 0 else 1 end 'num' "
					+ " FROM f_post as a "
					+ " LEFT JOIN f_account as b on a.aid = b.id "
					+ " LEFT JOIN f_theme as c on a.themeId = c.id "
					+ " left join f_finger as d on d.aid=? and a.id=d.postid "
					+ " WHERE a.isVaild =1 ORDER BY a.isTop DESC,a.ctime DESC LIMIT "+20*num+",20",aid);
		}
		else if(parentId.equals("page_2")){
			//关注
			searchList = Db.find("SELECT a.id,a.aid,a.content,a.img01,a.img02,a.img03,a.img04,a.img05,a.img06,a.img07,"
					+ " a.img08,a.img09,a.ctime,a.fingerNum,a.commentNum,a.isVaild,b.headimg,b.nick,c.title, "
					+ "case b.grade WHEN 1 then '嫩绿新叶' WHEN 2 then '可爱小苗' WHEN 3 then '亭亭玉立' WHEN 4 then '娇羞花蕾' WHEN 5 then '含苞待放' WHEN 6 then '华丽绽放' end 'grade', "
					+ " case when d.id is null then 0 else 1 end 'num' "
					+ " FROM f_post as a "
					+ " LEFT JOIN f_account as b on a.aid=b.id "
					+ " LEFT JOIN f_theme as c ON a.themeId = c.id "
					+ " left join f_finger as d on d.aid=? and a.id=d.postid "
					+ " WHERE a.themeId in(SELECT themeId FROM f_myfocus WHERE aid = ?) "
					+ " and a.isVaild =1 ORDER BY isTop DESC LIMIT "+20*num+",20",aid,aid);
		}
		else if(parentId.equals("page_3")){
			//精选
			searchList = Db.find("SELECT a.isVaild,a.isTop,a.id,a.themeId,c.title,a.content,a.img01,a.img02,a.img03,a.img04,a.img05,a.img06,a.img07,a.img08,a.img09,"
					+ " a.ctime,a.fingerNum,a.commentNum,b.headimg,b.nick, "
					+ "case b.grade WHEN 1 then '嫩绿新叶' WHEN 2 then '可爱小苗' WHEN 3 then '亭亭玉立' WHEN 4 then '娇羞花蕾' WHEN 5 then '含苞待放' WHEN 6 then '华丽绽放' end 'grade', "
					+ " case when d.id is null then 0 else 1 end 'num' FROM f_post as a "
					+ " LEFT JOIN f_account as b ON a.aid=b.id"
					+ " LEFT JOIN f_theme as c ON a.themeId = c.id "
					+ " left join f_finger as d on d.aid= ? and a.id=d.postid "
					+ " WHERE a.isVaild =1 and a.isSelected=1 ORDER BY a.isTop DESC,a.ctime DESC LIMIT "+20*num+",20",aid);			
		}
		else if(parentId.equals("page_4")){
			//我的圈子
			searchList = Db.find("SELECT a.id,a.aid,a.content,a.img01,a.img02,a.img03,a.img04,a.img05,a.img06,a.img07,a.img08,a.img09,"
					+ " case when d.id is null then 0 else 1 end 'num',"
					+ " a.ctime,a.fingerNum,a.commentNum,a.type,a.themeId,a.isVaild,b.headimg,b.nick,c.title,"
					+ "case b.grade WHEN 1 then '嫩绿新叶' WHEN 2 then '可爱小苗' WHEN 3 then '亭亭玉立' WHEN 4 then '娇羞花蕾' WHEN 5 then '含苞待放' WHEN 6 then '华丽绽放' end 'grade' "
					+ " FROM f_post as a"
					+ " LEFT JOIN f_account as b on a.aid = b.id"
					+ " LEFT JOIN f_theme as c on a.themeId = c.id"
					+ " left join f_finger as d on d.aid=? and a.id=d.postid "
					+ " WHERE a.isVaild =1 and a.aid=? ORDER BY a.isTop DESC,a.ctime DESC LIMIT "+20*num+",20",aid,aid);
		}
		else if(parentId.equals("page_5")){
			//我回复的
			searchList = Db.find("SELECT a.id,a.themeId,c.title,a.content,a.img01,a.img02,a.img03,a.img04,a.img05,a.img06,a.img07,a.img08,a.img09,"
					+" a.ctime,a.fingerNum,a.tipsNum,a.commentNum,b.headimg,b.nick,"
					+" case b.grade WHEN 1 then '嫩绿新叶' WHEN 2 then '可爱小苗' WHEN 3 then '亭亭玉立' WHEN 4 then '娇羞花蕾' WHEN 5 then '含苞待放' WHEN 6 then '华丽绽放' end 'grade'," 
					+" case when d.id is null then 0 else 1 end 'num' "
					+" FROM f_post as a LEFT JOIN f_account as b ON a.aid=b.id "
					+" LEFT JOIN f_theme as c ON a.themeId = c.id "
					+" left join f_finger as d on a.id=d.postid and d.aid=?"
					+" where a.id in ( select postId from f_comments  where aid=?"
					+" UNION (select b.postId from f_reply as a left join f_comments as b "
					+" on a.comId=b.id where a.aid=?)) LIMIT "+20*num+",20",aid,aid,aid);
		}
		else if(parentId.equals("page_6")){
			//我打赏的
			searchList = Db.find("SELECT a.id,a.themeId,c.title,a.content,a.img01,a.img02,a.img03,a.img04,a.img05,a.img06,a.img07,a.img08,a.img09,"
				+ "a.ctime,a.fingerNum,a.tipsNum,a.commentNum,b.headimg,b.nick, "
				+ "case b.grade WHEN 1 then '嫩绿新叶' WHEN 2 then '可爱小苗' WHEN 3 then '亭亭玉立' WHEN 4 then '娇羞花蕾' WHEN 5 then '含苞待放' WHEN 6 then '华丽绽放' end 'grade',  "
				+ "case when d.id is null then 0 else 1 end 'num' FROM f_post as a LEFT JOIN f_account as b ON a.aid=b.id  "
				+ "LEFT JOIN f_theme as c ON a.themeId = c.id left join f_finger as d on d.aid=? and a.id=d.postid  "
				+ "LEFT JOIN f_tips as e on a.id=e.postId  "
				+ " WHERE a.isVaild =1 and e.tipAid=? ORDER BY e.ctime DESC LIMIT "+20*num+",20",aid,aid);
		}
		
		Map<String, Object> result = new HashMap<>();
		if (searchList.size() > 0 && searchList !=  null) {
			result.put("flat", true);
			result.put("searchList", searchList);
		}
		
		renderJson(result);
	}
	
	/**
	 * 帖子详情
	 */
	public void detail() {
		int id=getParaToInt(0);//帖子ID号
		Record account = getSessionAttr("account");
		int aid=account.getInt("id");
		Record detail = IndexDao.getDetail(aid,id);
		
		List<Record> fingerimg = IndexDao.getFinger(id);
		List<Record> tipsimg = IndexDao.getTips(id);
		List<Record> comments = IndexDao.getComments(aid,id);//所有评论
		List<Record> replylist=IndexDao.getReply();//抓出所有的【回复】
		setAttr("fingerimg", fingerimg);
		setAttr("tipsimg", tipsimg);
		setAttr("comments", comments);
		setAttr("replylist", replylist);
		setAttr("detail", detail);
		render("detail.html");
	}
	
	/**
	 *  保存收藏
	 */
	public void saveCollect(){
		Record user= getSessionAttr("account");
		Integer postId = getParaToInt(0);
		Record collect=Db.findFirst("SELECT * FROM f_mycollect WHERE postId=? and aid=?",postId,user.getInt("id"));
		if(collect==null){
			Record f_mycollect = new Record();
			f_mycollect.set("aid",user.getInt("id"));
			f_mycollect.set("postId",postId);
			f_mycollect.set("sTime",new Date());
			f_mycollect.set("state",1);
			Db.save("f_mycollect", f_mycollect);
		}else {
			Db.update("update f_mycollect set state=1,sTime=now() where postId=?",postId);
		}
		Db.update("update f_post set collectNum=collectNum+1 where id=?", postId);
		Map<String, Object> map = new HashMap<>();
		renderJson(map);
	}
	
	
	/**
	 *  取消收藏
	 */
	public void cancelCollect(){
		Integer postId = getParaToInt(0);
		Db.update("update f_mycollect set state=0,eTime=now() where postId=?",postId);
		Db.update("update f_post set collectNum=collectNum-1 where id=?", postId);
		Map<String, Object> map = new HashMap<>();
		renderJson(map);
	}
		
	/**
	 *  话题列表
	 */
	public void topiclist() {
		List<Record> topiclist = IndexDao.getTopiclist();
		setAttr("topiclist", topiclist);
		render("topiclist.html");
	}
	
	/**
	 *  话题帖子详情
	 */
	public void topic_detail() {
		int id=getParaToInt(0);//话题ID号
		Record topic = IndexDao.getTopic(id);
		Record users = getSessionAttr("account");
		int aid=users.getInt("id");
		List<Record> details = IndexDao.getTopicDetail(aid,id);
		
		List<Record> comlist=IndexDao.getCom();//抓出所有帖子的前两条评论
		setAttr("comlist", comlist);
		
		int state=0;//默认为没有关注
		Record stateRd=Db.findFirst("SELECT state FROM f_myfocus WHERE themeId=? and aid=?",id,aid);
		if(stateRd!=null&&stateRd.getInt("state")==1){
			state=1;
		}
		setAttr("state", state);
		setAttr("topic", topic);
		setAttr("details", details);
		render("topic_detail.html");
	}
	
	/**
	 *  保存关注
	 */
	public void saveFocus(){
		Record user= getSessionAttr("account");
		Integer themeId = getParaToInt(0);//话题ID号
		Record collect=Db.findFirst("SELECT * FROM f_myfocus WHERE themeId=? and aid=?",themeId,user.getInt("id"));
		if(collect==null){
			Record f_myfocus = new Record();
			f_myfocus.set("aid",user.getInt("id"));
			f_myfocus.set("themeId",themeId);
			f_myfocus.set("sTime",new Date());
			f_myfocus.set("state",1);
			Db.save("f_myfocus", f_myfocus);
		}else {
			Db.update("update f_myfocus set state=1,sTime=now() where themeId=?",themeId);
		}
		Db.update("update f_theme set focusNum=focusNum+1 where id=?", themeId);
		Map<String, Object> map = new HashMap<>();
		renderJson(map);
	}
	
	
	
	/**
	 *  取消关注
	 */
	public void cancelFocus(){
		Integer themeId = getParaToInt(0);
		Db.update("update f_myfocus set state=0,eTime=now() where themeId=?",themeId);
		Db.update("update f_theme set focusNum=focusNum-1 where id=?", themeId);
		Map<String, Object> map = new HashMap<>();
		renderJson(map);
	}
	
	/**
	 *  我的
	 */
	public void myinfo() {
		Record account = getSessionAttr("account");
		int id=account.getInt("id");
		Record myInfo = IndexDao.getInfo(id);
		List<Record> Mypost = IndexDao.getMypost(id);
		List<Record> Myreply = IndexDao.getMyreply(id);
		List<Record> Mytips = IndexDao.getMytips(id);
		List<Record> comlist=IndexDao.getCom();//抓出所有帖子的前两条评论
		setAttr("comlist", comlist);
		setAttr("myinfo", myInfo);
		setAttr("Mypost", Mypost);
		setAttr("Myreply", Myreply);
		setAttr("Mytips", Mytips);
		
		//setAttr("myIntegral", myIntegral);
		render("myinfo.html");
	}
	
	/**
	 *  发帖人删除帖子,把它变成无效帖
	 */
	public void dele(){
		Integer postId = getParaToInt(0);
		Db.update("update f_post set isVaild=0 where id=?", postId);
		Db.update("update f_theme set postNum=postNum-1 where id=(SELECT themeId FROM f_post WHERE id=?)", postId);
		Map<String, Object> map = new HashMap<>();
		renderJson(map);
	}
	
	/**
	 * 发帖提交
	 */
	public void submitPost(){
		
		int themeId=getParaToInt("themeId")==null?0:getParaToInt("themeId");//话题ID号
		String content=getPara("content");//文字内容
		String imgurls=getPara("imgurls");//图片路径
		
		//System.out.println(imgurls);
		
		String[] imgurllist=imgurls.split(",");
		
		Record account = getSessionAttr("account");
		Record f_post=new Record();
		f_post.set("aid", account.getInt("id"));
		f_post.set("content", content);
		//如果有图片
		if(imgurllist.length>0){
			for(int i=0;i<imgurllist.length-1;i++){
				f_post.set("img0"+(i+1), imgurllist[i]);
			}
		}
		f_post.set("ctime", new Date());//发帖时间
		f_post.set("type", (themeId==0)?0:1);
		f_post.set("themeId", themeId);
		boolean flag= Db.save("f_post", f_post);
		
		Map<String, Object> map = new HashMap<>();
		map.put("result", flag);
		map.put("msg", flag==true?"提交成功":"提交失败");
		renderJson(map);
	}
	
	/**
	 * 点赞提交
	 */
	public void submitFinger(){
		Integer postId = getParaToInt(0);
		Record account = getSessionAttr("account");
		Map<String, Object> map = new HashMap<>();
		Record finger=Db.findFirst("SELECT * FROM f_finger WHERE postId=? and aid=?",postId,account.getInt("id"));
		if(finger==null){
			Record f_finger=new Record();
			f_finger.set("postId", postId);
			f_finger.set("aid", account.getInt("id"));
			f_finger.set("ctime", new Date());
			Db.save("f_finger", f_finger);
			Db.update("update f_post set fingerNum=fingerNum+1 where id=?",postId);
			String headimg = Db.queryStr("SELECT b.headimg from f_finger a  LEFT JOIN f_account b on a.aid = b.id  WHERE a.aid =?",account.getInt("id"));
			map.put("headimg", headimg);
		}
		renderJson(map);
	}
	
	/**
	 * 评论提交
	 * @throws Exception  
	 */
	public void submitComments() throws Exception {
		Integer postId = getParaToInt(0);//帖子ID号
		String comStr=getPara(1);//评论内容
		comStr =URLDecoder.decode(comStr, "UTF-8");
		
		Record account = getSessionAttr("account");
		String openid1=account.getStr("openid");
		
		Record f_comments=new Record();
		f_comments.set("postId", postId);
		f_comments.set("aid", account.getInt("id"));
		f_comments.set("comStr", comStr);
		f_comments.set("ctime", new Date());
		f_comments.set("isValid", 1);
		boolean flag= Db.save("f_comments", f_comments);
		Number id=0;
		if(flag==true){
			//帖子的【评论人数】加1
			flag=Db.update("update f_post set commentNum=commentNum+1 where id=?", postId)==1?true:false;
			id=f_comments.getNumber("id");
		}
		Map<String, Object> map = new HashMap<>();
		map.put("result", flag);
		map.put("msg", flag==true?"提交成功":"提交失败");
		Record com = Db.findFirst("SELECT a.id,a.comStr,a.fingerNum,a.ctime,b.headimg,b.nick,b.grade from f_comments as a LEFT JOIN f_account as b on a.aid=b.id WHERE a.id=?",id.intValue());
		map.put("com", com);
		
		Integer aid = Db.queryInt("SELECT aid FROM f_post WHERE id = ?",postId);
		String openid2 = Db.queryStr("SELECT openid FROM f_account WHERE id = ?",aid);
		
		if(openid1.equals(openid2)==false){
			// 绑定 apiConfig 到当前线程
			WeixinApiCtrl.setApiConfig();
			//给提问人发模板消息（服务状态提醒）
			ApiResult result = TemplateMsgApi.send(TemplateData.New()
					// 消息接收者
					.setTouser(openid2)
					// 模板id
					.setTemplate_id(WeixinApiCtrl.gettemplateId("服务状态提醒"))
					.setTopcolor("#eb414a")
					.setUrl(Constant.getHostBbs + "/detail/"+postId)
					.add("first", "有一位花粉评论了你的帖子，去看看吧", "#FF8040")
					.add("keyword1", "社区提醒", "#FF8040")
					.add("keyword2", "已评论", "#FF8040").build());
			//WeixinApiCtrl.sendTemplateMsg(result.getJson());
		}
		
		
		renderJson(map);
	}
	
	/**
	 * 对评论点赞
	 */
	public void CommentsFinger(){
		Integer comId = getParaToInt(0);
		Record account = getSessionAttr("account");
		Map<String, Object> map = new HashMap<>();
		Record finger=Db.findFirst("SELECT * FROM f_finger_c WHERE comId=? and aid=?",comId,account.getInt("id"));
		if(finger==null){
			Record f_finger_c=new Record();
			f_finger_c.set("comId", comId);
			f_finger_c.set("aid", account.getInt("id"));
			f_finger_c.set("ctime", new Date());
			Db.save("f_finger_c", f_finger_c);
			Db.update("update f_comments set fingerNum=fingerNum+1 where id=?",comId);
		}
		renderJson(map);
	}

	/**
	 * 跟帖提交
	 * @throws Exception 
	 */
	public void submitReply() throws Exception{
		Integer postId = getParaToInt("postId");//帖子ID号
		Integer comId = getParaToInt("comId");//评论ID号
		String replyStr=getPara("replyStr");//回复内容
		replyStr =URLDecoder.decode(replyStr, "UTF-8");
		
		Record account = getSessionAttr("account");
		String openid1=account.getStr("openid");
		
		Record f_reply=new Record();
		f_reply.set("comId", comId);
		f_reply.set("aid",account.getInt("id"));
		f_reply.set("replyStr", replyStr);
		f_reply.set("ctime", new Date());
		f_reply.set("isValid", 1);
		boolean flag= Db.save("f_reply", f_reply);
		Number id=0;
		if(flag==true){
			//帖子的【跟帖人数】加1
			flag=Db.update("update f_comments set replyNum=replyNum+1 where id=?", comId)==1?true:false;
			id=f_reply.getNumber("id");
		}
		Map<String, Object> map = new HashMap<>();
		map.put("result", flag);
		map.put("msg", flag==true?"提交成功":"提交失败");
		Record reply = Db.findFirst("SELECT a.replyStr,b.nick from f_reply as a LEFT JOIN f_account as b on a.aid=b.id WHERE a.id=?",id.intValue());
		map.put("reply", reply);
		
		Integer comAid = Db.queryInt("SELECT aid FROM f_comments WHERE id = ?",comId);
		String openid2 = Db.queryStr("SELECT openid FROM f_account WHERE id = ?",comAid);
		
		if(openid1.equals(openid2)==false){
			// 绑定 apiConfig 到当前线程
			WeixinApiCtrl.setApiConfig();
			//给提问人发模板消息（服务状态提醒）
			ApiResult result = TemplateMsgApi.send(TemplateData.New()
					// 消息接收者
					.setTouser(openid2)
					// 模板id
					.setTemplate_id(WeixinApiCtrl.gettemplateId("服务状态提醒"))
					.setTopcolor("#eb414a")
					.setUrl(Constant.getHostBbs + "/detail/"+postId)
					.add("first", "有一位花粉回复了您的评论，去看看吧", "#FF8040")
					.add("keyword1", "社区提醒", "#FF8040")
					.add("keyword2", "已回复", "#FF8040").build());
			//WeixinApiCtrl.sendTemplateMsg(result.getJson());
		}
		renderJson(map);
	}
	
	/**
	 * 收藏提交
	 */
	public void submitCollect(){
		
		int postId=getParaToInt("postId");//帖子的ID号
		Record account = getSessionAttr("account");
		
		Record f_mycollect=new Record();
		f_mycollect.set("aid", account.getInt("id"));
		f_mycollect.set("postId", postId);
		f_mycollect.set("sTime", new Date());
		f_mycollect.set("state",1);
		
		boolean flag= Db.save("f_mycollect", f_mycollect);
		if(flag==true){
			//帖子的【收藏人数】加1
			flag=Db.update("update f_post set collectNum=collectNum+1 where id=?", postId)==1?true:false;
		}
		Map<String, Object> map = new HashMap<>();
		map.put("result", flag);
		map.put("msg", flag==true?"提交成功":"提交失败");
		renderJson(map);
	}
	
	/**
	 * 发送图文消息
	 * @author Glacier
	 * @date 2017年11月10日
	 */
	public void editor() {
		List<Record> topiclist = IndexDao.getTopiclist();
		setAttr("topiclist", topiclist);
		render("editor.html");
	}
	
	/**
	 * 获取token
	 * @Title: upToken
	 * @author: Glacier
	 * @return: void
	 */
	public void upToken() {
		
		Map<String, Object> resultMap = new HashMap<>();
        String token = QianNiuUpload.getUpToken();
        
        resultMap.put("uptoken",token);
        renderJson(resultMap);
	}
	
	/**
	 * 上传图片到七牛云
	 * 提交帖子到数据库
	 * @author Glacier
	 * @throws IOException 
	 * @date 2017年11月13日
	 */
	public void qiniuImg() throws IOException {
		int tid=getParaToInt("tid")==null?0:getParaToInt("tid");//针对话题贴有效
		String[] resImgurl = getParaValues("resImgurl[]");//这个写法比较特别，第一次遇见：前台数据参数名：resImgurl，后台获取时用resImgurl[]
		//写入数据库
		Record account = getSessionAttr("account");
	 	int aid = account.getInt("id");
		String content = getPara("content")==null?"信息居然为空啦！":getPara("content");
		//应该给用户发多少积分
		String score=Db.queryStr("select code_value from f_dictionary "
				+ "where code_key in(select CONCAT('integral0',grade) grade from f_account where id=?)",aid);
		Record f_post = new Record();
		f_post.set("themeId", tid);
		f_post.set("aid", aid);
		f_post.set("content", content);
		f_post.set("ctime", new Date());//发帖时间
		f_post.set("scoreNum", Integer.valueOf(score));//积分
		
		//此处用的是e合作社的配置
		//之前用了花美美配置，但是access_token未做共享机制，导致社区的access_token经常失效
		/*WeixinApiCtrl.setApiConfig();
		String token = AccessTokenApi.getAccessToken().getAccessToken();*/
		
		
		//String token=HttpUtils.get(Constant.getTokenUrl);//从代理获取access_token
		
		// 如果有图片
		if(resImgurl!=null&&resImgurl.length!=0){
			//获取图片信息
			List<String> imgurl =  new ArrayList<String>();
			int j = 1;
			
			
			
			for (String media_id : resImgurl) {
				imgurl.add(QianNiuUpload.upload(media_id));
			}
			for (String img : imgurl) {
				f_post.set("img0"+(j++), img);
			}
		}
		
		boolean flag = Db.save("f_post", f_post);
		
		
		
		if(flag==true){
			//如果是话题贴，话题的【发帖数量】加1
			Db.update("update f_theme set postNum=postNum+1 where id=?", tid);
			//根据会员等级送积分
			Number pid = f_post.getLong("id");//帖子的ID号
			for(int i=0;i<Integer.valueOf(score);i++){
				Record f_myIntegral=new Record();
				f_myIntegral.set("aid", aid);
				f_myIntegral.set("pid", pid.intValue());
				f_myIntegral.set("score", 1);
				f_myIntegral.set("gTime", new Date());
				Db.save("f_myIntegral", f_myIntegral);
			}
		}
		
		
		Map<String, Object> result = new HashMap<>();
		
		if (flag) {
			result.put("re", true);
			result.put("msg", "发布成功了");
		}else {
			result.put("re", false);
			result.put("msg", "发布失败啦");
		}
		
		renderJson(result);
	}
	
	
	/**
	 * api
	 * @author Glacier
	 * @date 2017年12月13日
	 */
	public void v1() {
		// https://api.example.com/v1/zoos
		
	}
	
	/**
	 * index2
	 * @author Glacier
	 * @date 2017年12月13日
	 */
	public  void  index2() {
		render("index2.html");
	}
	public  String getRemortIP(HttpServletRequest request) {
		if (request.getHeader("x-forwarded-for") == null) {
			return request.getRemoteAddr(); 
		}
		String ipStr = request.getHeader("x-forwarded-for");
		String[] ipArr = ipStr.split(",");
		String ip = new String();
		for(String i : ipArr){
			if(!"unknown".equals(ip)){
				ip = i;
				break;
			}
		}
		return ip;
	}
	/**
	 * 创建打赏订单，完成支付
	 * @param postId 帖子id
	 * @param money  打赏金额
	 */
	public  void createOrderThis(){
		int postId=getParaToInt("postId");
		double tipMoney=Double.valueOf(getPara("tipMoney"));
        Record account = getSessionAttr("account");
		String mchBillno ="DS"+System.currentTimeMillis() + "";//订单编号
		Map<String,Object> resultAttr=new  HashMap<String,Object>();
		Record f_tips=new Record();
		f_tips.set("tipCode", mchBillno);
		f_tips.set("tipAid", account.getInt("id"));
		f_tips.set("postId", postId);
		f_tips.set("tipMoney", tipMoney);
		f_tips.set("cTime",new Date());
		if(Db.save("f_tips", f_tips)){
			String xml = GiveTip.wxPushOrder("帖子打赏", mchBillno,
					tipMoney, getRemortIP(getRequest()), account.getStr("openid"));
			try {
				Map<String, Object> resultMap = XMLParser.getMapFromXML(xml);
				if (resultMap.get("result_code").equals("SUCCESS")) {
					resultAttr.put("mchBillno", mchBillno);
					resultAttr.put("postId", postId);
					resultAttr.put("payMap", Sign.sign2(resultMap.get("prepay_id").toString()));
					renderJson(resultAttr);					
				} else {
					render("error.html");
				}
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	
	
	
	public  void createOrder(){
		int postId=getParaToInt("postId");
		double tipMoney=Double.valueOf(getPara("tipMoney"));
        Record account = getSessionAttr("account");
		String mchBillno ="DS"+System.currentTimeMillis() + "";//订单编号
		Record f_tips=new Record();
		f_tips.set("tipCode", mchBillno);
		f_tips.set("tipAid", account.getInt("id"));
		f_tips.set("postId", postId);
		f_tips.set("tipMoney", tipMoney);
		f_tips.set("cTime",new Date());
		if(Db.save("f_tips", f_tips)){
			String xml = GiveTip.wxPushOrder("帖子打赏", mchBillno,
					tipMoney, getRemortIP(getRequest()), account.getStr("openid"));
			//System.out.println(xml);
			try {
				Map<String, Object> resultMap = XMLParser.getMapFromXML(xml);
				if (resultMap.get("result_code").equals("SUCCESS")) {
					setAttr("mchBillno", mchBillno);
					setAttr("postId", postId);
					setAttr("payMap", Sign.sign2(resultMap.get("prepay_id").toString()));
					System.out.println(Sign.sign2(resultMap.get("prepay_id").toString()));					
					render("topay.html");
				} else {
					render("error.html");
				}
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * 我的零钱
	 */
	public void mywallet() {
		Record account = getSessionAttr("account");
		setAttr("wallet",Db.queryDouble("SELECT wallet from f_account WHERE id=?",account.getInt("id")));
		render("mywallet.html");
	}
	
	/**
	 * 零钱提现
	 */
	public void tixian() {
		Record account = getSessionAttr("account");
		setAttr("wallet",Db.queryDouble("SELECT wallet from f_account WHERE id=?",account.getInt("id")));
		render("tixian.html");
	}
	
    public void tixianResult(){
    	Record account = getSessionAttr("account");
    	int money=getParaToInt(0);
        boolean flag= WithdrawCash.ComToUser(getRequest(), account.getStr("openid"),
        	    	String.valueOf(((int)money)) ,"打赏提现");
        double oldMoney=Db.queryDouble("select wallet from f_account where openid=?",account.getStr("openid"));
        //记录提现日志
        if(flag){
        	Db.tx(new IAtom() {
				
				@Override
				public boolean run() throws SQLException {
					Db.update("update f_account set wallet=wallet-? where openid=?",money/100.0,account.get("openid"));
				    Record f_withdrawCash=new Record();
				    f_withdrawCash.set("aid",account.get("id"));
				    f_withdrawCash.set("tMoney",money/100.0);
				    f_withdrawCash.set("cTime",new Date());
				    f_withdrawCash.set("sTime",new Date());
				    f_withdrawCash.set("state",1);
				    Db.save("f_withdrawCash", f_withdrawCash);
				    
				    
				    Record f_updateWallet=new Record();
				    f_updateWallet.set("aid", account.get("id"));
				    f_updateWallet.set("iMoney", money/100.0);
				    f_updateWallet.set("cTime", new Date());
				    f_updateWallet.set("restMoney", oldMoney- money/100.0);
				    f_updateWallet.set("type", 21);
				    f_updateWallet.set("state", 2);
				    Db.save("f_updateWallet", f_updateWallet);
					return true;
				}
			});
        }
        renderJson(flag);
    }
	
	/**
	 * 零钱明细
	 */
	public void wallet_detail() {
		Record account = getSessionAttr("account");
		int aid=account.getInt("id");
		List<Record> walletlist = IndexDao.getMywallet(aid);
		setAttr("walletlist", walletlist);
		render("wallet_detail.html");
	}
	
}

package com.flower.Dao;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class IndexDao {
	
	/**
	 * 获取提交的话题
	 * @author Glacier
	 * @date 2017年11月15日
	 * @return
	 */
	public static List<Record> getAllpost(int aid) {
		return Db.find("SELECT a.isVaild,a.isTop,a.id,a.themeId,c.title,a.content,a.img01,a.img02,a.img03,a.img04,a.img05,a.img06,a.img07,a.img08,a.img09,a.ctime,a.fingerNum,a.tipsNum,a.commentNum,b.headimg,b.nick,"
				+ "case b.grade WHEN 1 then '嫩绿新叶' WHEN 2 then '可爱小苗' WHEN 3 then '亭亭玉立' WHEN 4 then '娇羞花蕾' WHEN 5 then '含苞待放' WHEN 6 then '华丽绽放' end 'grade', "
				+ "case when d.id is null then 0 else 1 end 'num' FROM f_post as a LEFT JOIN f_account as b ON a.aid=b.id "
				+ "	LEFT JOIN f_theme as c ON a.themeId = c.id left join f_finger as d on d.aid=? and a.id=d.postid "
				+ " WHERE a.isVaild =1 ORDER BY a.isTop DESC,a.ctime DESC LIMIT 20",aid);
	}
	
	/**
	 * 获取精选帖
	 * @author Glacier
	 * @date 2017年12月20日
	 * @return
	 */
	public static List<Record> getJx(int aid) {
		return Db.find("SELECT a.isVaild,a.isTop,a.id,a.themeId,c.title,a.content,a.img01,a.img02,a.img03,a.img04,a.img05,a.img06,a.img07,a.img08,a.img09,"
				+ " a.ctime,a.fingerNum,a.tipsNum,a.commentNum,b.headimg,b.nick, "
				+ "case b.grade WHEN 1 then '嫩绿新叶' WHEN 2 then '可爱小苗' WHEN 3 then '亭亭玉立' WHEN 4 then '娇羞花蕾' WHEN 5 then '含苞待放' WHEN 6 then '华丽绽放' end 'grade', "
				+ " case when d.id is null then 0 else 1 end 'num' FROM f_post as a "
				+ " LEFT JOIN f_account as b ON a.aid=b.id"
				+ " LEFT JOIN f_theme as c ON a.themeId = c.id "
				+ " left join f_finger as d on d.aid= ? and a.id=d.postid "
				+ " WHERE a.isVaild =1 and a.isSelected=1 ORDER BY a.isTop DESC,a.ctime DESC LIMIT 20",aid);
	}
	
	
	/**
	 * 获取我关注的话题的帖子
	 * @return
	 */
	public static List<Record> getMyfocus(int aid) {
		return Db.find("SELECT a.id,a.aid,a.content,a.img01,a.img02,a.img03,a.img04,a.img05,a.img06,a.img07,a.img08,a.img09,"
				+ "a.ctime,a.fingerNum,a.tipsNum,a.commentNum,a.isVaild,b.headimg,b.nick,c.title, "
				+ "case b.grade WHEN 1 then '嫩绿新叶' WHEN 2 then '可爱小苗' WHEN 3 then '亭亭玉立' WHEN 4 then '娇羞花蕾' WHEN 5 then '含苞待放' WHEN 6 then '华丽绽放' end 'grade', "
				+ "case when d.id is null then 0 else 1 end 'num' FROM f_post as a LEFT JOIN f_account as b on a.aid=b.id LEFT JOIN f_theme as c ON a.themeId = c.id left join f_finger as d on d.aid=? and a.id=d.postid "
				+ "WHERE a.themeId in(SELECT themeId FROM f_myfocus WHERE aid = ?) and a.isVaild =1 ORDER BY isTop DESC LIMIT 20",aid,aid);
	}
	
	/**
	 * 获取每条帖子的前两条评论
	 * @return
	 */
	public static  List<Record> getCom() {
		return  Db.find("SELECT a.postId,a.comStr,b.nick from f_comments as a LEFT JOIN f_account as b on a.aid=b.id LEFT JOIN f_post as c on a.postId=c.id where a.isValid =1");
	}
	
	/**
	 * 获取话题详情
	 * @return
	 */
	public static List<Record> getTopicDetail(int aid,int id) {
		return Db.find("SELECT a.id,a.content,a.img01,a.img02,a.img03,a.img04,a.img05,a.img06,a.img07,a.img08,a.img09,a.ctime,a.fingerNum,a.tipsNum,a.commentNum,b.headimg,b.nick,"
				+ "case b.grade WHEN 1 then '嫩绿新叶' WHEN 2 then '可爱小苗' WHEN 3 then '亭亭玉立' WHEN 4 then '娇羞花蕾' WHEN 5 then '含苞待放' WHEN 6 then '华丽绽放' end 'grade', "
				+"case when c.id is null then 0 else 1 end 'num' FROM f_post as a LEFT JOIN f_account as b ON a.aid=b.id left join f_finger as c on c.aid=? and a.id=c.postid "
				+ "WHERE a.isVaild =1 and a.themeId=? ORDER BY id DESC LIMIT 20",aid,id);		
	}
	/**
	 * 获取帖子详情
	 * @return
	 */
	public static  Record getDetail(int aid,int id) {
		return Db.findFirst("SELECT a.id,a.content,a.img01,a.img02,a.img03,a.img04,a.img05,a.img06,a.img07,a.img08,a.img09,a.ctime,a.fingerNum,a.tipsNum,a.commentNum,b.headimg,b.nick,b.grade,c.title,"
						+ "	case when d.id is null then 0 else 1 end 'num' "
						+ "	FROM f_post as a LEFT JOIN f_account as b ON a.aid=b.id LEFT JOIN f_theme as c on a.themeId=c.id "
						+ "	left join f_finger as d on a.id=d.postid and d.aid=? "
						+ "	WHERE a.isVaild =1 and a.id=?",aid,id);		
	}
	
	/**
	 * 获取点赞详情
	 * @return
	 */
	public static  List<Record> getFinger(int id) {
		return  Db.find("SELECT b.headimg FROM f_finger as a LEFT JOIN f_account as b ON a.aid=b.id WHERE a.postId=?",id);
	}
	
	/**
	 * 获取打赏详情
	 * @return
	 */
	public static  List<Record> getTips(int id) {
		return  Db.find("SELECT b.headimg FROM f_tips as a LEFT JOIN f_account as b ON a.tipAid=b.id WHERE a.state=1 and a.postId=?",id);
	}
	
	/**
	 * 获取评论详情
	 * @return
	 */
	public static  List<Record> getComments(int aid,int id) {
		return  Db.find("SELECT a.id,b.headimg,b.nick,b.grade,a.ctime,a.comStr,a.fingerNum,case when c.id is null then 0 else 1 end 'comnum' "
				+" FROM f_comments as a LEFT JOIN f_account as b ON a.aid=b.id left join f_finger_c as c on c.aid=? and a.id=c.comId WHERE a.isValid =1 and a.postId=? ORDER BY a.ctime DESC",aid,id);
	}
	
	/**
	 * 获取评论的回复详情
	 * @return
	 */
	public static  List<Record> getReply() {
		return  Db.find("SELECT a.replyStr,b.nick,a.comId from f_reply as a LEFT JOIN f_account as b on a.aid=b.id LEFT JOIN f_comments as c on a.comId=c.id where a.isValid=1");
	}
	
	/**
	 * 获取话题列表
	 * @return
	 */
	public static  List<Record> getTopiclist() {
		return Db.find("SELECT id,imgurl,title,content,postNum FROM f_theme where isValid=1");		
	}
	
	/**
	 * 获取单个话题
	 * @return
	 */
	public static  Record getTopic(int id) {
		return Db.findFirst("SELECT id,imgurl,title,des,content,postNum,focusNum FROM f_theme where id=?",id);		
	}
	
	/**
	 * 获取我的数据
	 * @return
	 */
	public static Record getInfo(int id) {
		return Db.findFirst("SELECT a.id,a.nick,a.headimg,a.grade 'grade_a',CAST(a.wallet as decimal(18,2)) 'wallet',sum(b.scoreNum) 'score',sum(b.fingerNum) 'fingerNum',"
					+"case a.grade WHEN 1 then '嫩绿新叶' WHEN 2 then '可爱小苗' WHEN 3 then '亭亭玉立' WHEN 4 then '娇羞花蕾' WHEN 5 then '含苞待放' WHEN 6 then '华丽绽放' end 'grade_b' "
					+"from f_account as a LEFT JOIN f_post as b ON a.id=b.aid WHERE a.id=?",id);
	}
	
	/**
	 * 获取我的圈子
	 * @return
	 */
	public static  List<Record> getMypost(int id) {
		return Db.find("SELECT a.isVaild,a.isTop,a.id,a.themeId,c.title,a.content,a.img01,a.img02,a.img03,a.img04,a.img05,a.img06,a.img07,a.img08,a.img09,a.ctime,a.fingerNum,a.tipsNum,a.commentNum,b.headimg,b.nick,"
				+ "case b.grade WHEN 1 then '嫩绿新叶' WHEN 2 then '可爱小苗' WHEN 3 then '亭亭玉立' WHEN 4 then '娇羞花蕾' WHEN 5 then '含苞待放' WHEN 6 then '华丽绽放' end 'grade', "
				+ "case when d.id is null then 0 else 1 end 'num' FROM f_post as a LEFT JOIN f_account as b ON a.aid=b.id "
				+ "	LEFT JOIN f_theme as c ON a.themeId = c.id left join f_finger as d on d.aid=? and a.id=d.postid "
				+ " WHERE a.isVaild =1 and a.aid=? ORDER BY a.isTop DESC,a.ctime DESC LIMIT 20",id,id);			
	}
	
	/**
	 * 获取我回复的(包括评论)
	 * @return
	 */
	public static  List<Record> getMyreply(int id) {
		return Db.find("SELECT a.id,a.themeId,c.title,a.content,a.img01,a.img02,a.img03,a.img04,a.img05,a.img06,a.img07,a.img08,a.img09,"
					+" a.ctime,a.fingerNum,a.tipsNum,a.commentNum,b.headimg,b.nick,"
					+" case b.grade WHEN 1 then '嫩绿新叶' WHEN 2 then '可爱小苗' WHEN 3 then '亭亭玉立' WHEN 4 then '娇羞花蕾' WHEN 5 then '含苞待放' WHEN 6 then '华丽绽放' end 'grade'," 
					+" case when d.id is null then 0 else 1 end 'num' "
					+" FROM f_post as a LEFT JOIN f_account as b ON a.aid=b.id "
					+" LEFT JOIN f_theme as c ON a.themeId = c.id "
					+" left join f_finger as d on a.id=d.postid and d.aid=?"
					+" where a.id in ( select postId from f_comments  where aid=?"
					+" UNION (select b.postId from f_reply as a left join f_comments as b "
					+" on a.comId=b.id where a.aid=?)) LIMIT 20",id,id,id);		
	}
	
	/**
	 * 获取我打赏
	 * @return
	 */
	public static  List<Record> getMytips(int id) {
		return Db.find("SELECT a.id,a.themeId,c.title,a.content,a.img01,a.img02,a.img03,a.img04,a.img05,a.img06,a.img07,a.img08,a.img09,a.ctime,a.fingerNum,a.tipsNum,a.commentNum,b.headimg,b.nick, "
				+ "case b.grade WHEN 1 then '嫩绿新叶' WHEN 2 then '可爱小苗' WHEN 3 then '亭亭玉立' WHEN 4 then '娇羞花蕾' WHEN 5 then '含苞待放' WHEN 6 then '华丽绽放' end 'grade',  "
				+ "case when d.id is null then 0 else 1 end 'num' FROM f_post as a LEFT JOIN f_account as b ON a.aid=b.id  "
				+ "LEFT JOIN f_theme as c ON a.themeId = c.id left join f_finger as d on d.aid=? and a.id=d.postid  "
				+ "LEFT JOIN f_tips as e on a.id=e.postId  "
				+ " WHERE a.isVaild =1 and e.tipAid=? ORDER BY e.ctime DESC LIMIT 20",id,id);		
	}
	
	/**
	 * 获取零钱明细
	 * @return
	 */
	public static  List<Record> getMywallet(int aid) {
		return Db.find("SELECT id,iMoney,cTime,type,state from f_updateWallet WHERE aid=? ORDER BY ctime DESC",aid);		
	}
}

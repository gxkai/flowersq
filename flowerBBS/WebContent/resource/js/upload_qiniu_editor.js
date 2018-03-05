var token 
/* 页面加载完毕 发起请求获取七牛token*/
function getTokenMessage() {
	$.ajax({
		url: '/upToken',
		type: 'POST',
		data: {},
		cache: false,
		contentType: false,    //不可缺
		processData: false,    //不可缺
		dataType : 'json',
		success: function (data) {
			var obj = data;
			token = obj.uptoken
			console.log(token)
			uploaderReady(obj.uptoken); 
		  }
		});
}

$(document).ready(function(){
	getTokenMessage();
});  

var imgCount = 0
var imgResponse = []
var text 
var i = 0
function uploaderReady(token){
    var uploader = Qiniu.uploader({
        runtimes: 'html5,flash,html4',
        browse_button: 'pickfiles',     //上传按钮的ID
        container: 'btn-uploader',      //上传按钮的上级元素ID
        drop_element: 'btn-uploader',
        max_file_size: '100mb',         //最大文件限制
        flash_swf_url: '/static/js/plupload/Moxie.swf',
        dragdrop: false,
        chunk_size: '4mb',              //分块大小
        //uptoken_url: '',              //Ajax请求upToken的Url，**强烈建议设置**（服务端提供）
        uptoken: token,                 //若未指定uptoken_url,则必须指定 uptoken ,uptoken由其他程序生成
        // save_key: true,              // 默认 false。若在服务端生成uptoken的上传策略中指定了 `sava_key`，则开启，SDK在前端将不对key进行任何处理
        domain: 'http://ovhq5iw4e.bkt.clouddn.com/',    //自己的七牛云存储空间域名
        multi_selection: true,         //是否允许同时选择多文件
        filters: {
            mime_types: [               //文件类型过滤，这里限制为图片类型
                {title: "Image files", extensions: "jpg,jpeg,gif,png"}
            ]
        },
        auto_start: false,
        unique_names :true,             //自动生成文件名,如果值为false则保留原文件名上传
        init: {
            'FilesAdded': function (up, files) {
           /*     
            	
          		for (var i = 0; i < files.length; i++) {
          		  
          			// 超过9个隐藏hide
          			imgCount =  $('#img_div').children('img').length;
          			console.log(imgCount)
          			if(imgCount == 9){
          				$('#add_img1').hide()
          			} 
          			
                    var fileItem = files[i].getNative(),
                          url = window.URL || window.webkitURL || window.mozURL;
                    var src = url.createObjectURL(fileItem);
                    var tmpl = '<img class="upload_img" src="#url#" ></img>';
                    $("#uploaderInput").before($(tmpl.replace(/#url#/ig, src)));
                }
            	
          		
            	*/
            },
            'BeforeUpload': function (up, file) {
                // 每个文件上传前，处理相关的事情
            },
            'UploadProgress': function (up, file) {
                //文件上传时，处理相关的事情

                /*可能是文件大小
                var chunk_size = plupload.parseSize(this.getOption('chunk_size'));
                */

                //上传进度 class="layui-btn" type="button"
                $('#qiniupercent').attr('style','width:'+file.percent+'%');
                //console.log(file.percent + "%");
            },
            'UploadComplete': function () {
            	 //队列文件处理完毕后，处理相关的事情
            	 
            	 
            	/*  
            	 $('input[name="imgurls"]').val(imgResponse);
            	 $('input[name="content"]').val(text);
            	 
            	 var result = $("input[id='imgurls']").attr("value");  */
            	 
            	var imgurls =  imgResponse.join(",")
            	console.log(imgurls)
            	 
				$.ajax({
		       	 	url: '/submitPost',
					data: {
						"themeId":"0",
 						"content":text.trim(),
 						"imgurls":imgurls
					},
					type: 'post',
					dataType: 'json',
					cache: false,
					success: function(data){
 						console.log(data)
 						if(data.result){
 							
 							console.log("成功写入数据库，进行页面跳转！")
 							location.replace('/index');
 						}else{
 							alert(data.msg)
 						}
 					  }
 					}); 
            },
            'FileUploaded': function (up, file, info) {
                //每个文件上传成功后,处理相关的事情
                //其中 info 是文件上传成功后，服务端返回的json，形式如
                //{
                //  "hash": "Fh8xVqod2MQ1mocfI4S4KpRL6D98",
                //  "key": "gogopher.jpg"
                //}
                
                var domain = up.getOption('domain');
                var data = eval('(' + info.response + ')');
                
                var imgurl = domain + data.key
                
                imgResponse[i] = imgurl;
                
                i += 1;
               /*
				console.log(i)
                console.log(imgResponse) */ 
                
                /* $('#img_div').append("<img class='upload_img' src='"+ imgurl +"'/>") */
                
                
               /*  var res = eval('(' + info + ')');
                console.log(res)
                
                var sourceLink = domain + res.key;//获取上传文件的链接地址
                console.log(sourceLink); */
            },
            'Error': function (up, err, errTip) {
                alert(errTip);
            },
            'Key': function (up, file) {
                //当save_key和unique_names设为false时，该方法将被调用
                var key = "";
                $.ajax({
                    url: '/qiniu-token/get-key/',
                    type: 'GET',
                    async: false,//这里应设置为同步的方式
                    success: function (data) {
                        var ext = Qiniu.getFileExtension(file.name);
                        key = data + '.' + ext;
                    },
                    cache: false
                });
                return key;
            }
        }
    });
    $('#upload_img1').on('click',function(){
    	
    	console.log("进行判断")
    	
    	text = $('#text').text();
    	
		if(text.trim() == null || text.trim() == ''){
			console.log('请填写数据')
			return false;
		}
		
		//如果没有图片，就是单纯的表单提交了
		
		
		
    	uploader.start();
    });
    
}
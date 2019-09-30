package cn.jsuacm.ccw.service.projects.impl;

import cn.jsuacm.ccw.mapper.projects.CommitMapper;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.projects.Commit;
import cn.jsuacm.ccw.service.projects.CommitService;
import cn.jsuacm.ccw.util.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName CommitServiceImpl
 * @Description TODO
 * @Author h4795
 * @Date 2019/08/01 10:56
 */
@Service

public class CommitServiceImpl extends ServiceImpl<CommitMapper, Commit> implements CommitService{


    @Autowired
    @Qualifier(value = "remoteRestTemplate")
    private RestTemplate restTemplate;



    @Autowired
    private CommitMapper commitMapper ;

    @Override
    public List<Commit> getByGitHub(String url, String username) {
        List<Commit> list = new ArrayList<>();
        try {
            ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class);
            List<Map<String, Object>> values = null;
            values = new ObjectMapper().readValue(entity.getBody(), List.class);
            for (Map<String, Object> value : values){

                Map<String, Object> commitMap = (Map<String, Object>) value.get("commit");
                Map<String, Object> committer = (Map<String, Object>) commitMap.get("committer");

                String name = String.valueOf(committer.get("name"));
                if (!username.equals(name)){
                    continue;
                }
                String email = String.valueOf(committer.get("email"));
                String utcTime = String.valueOf(committer.get("date"));
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                //设置时区UTC
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                //格式化，转当地时区时间
                Date after = null;
                after = df.parse(utcTime);
                String sha = String.valueOf(value.get("sha"));
                String htmlUrl = String.valueOf(value.get("html_url"));
                String message = String.valueOf(commitMap.get("message"));
                Commit commit = new Commit();
                commit.setCid(1);
                commit.setName(name);
                commit.setEmail(email);
                commit.setDate(after);
                commit.setMessage(message);
                commit.setSha(sha);
                commit.setHtmlUrl(htmlUrl);
                list.add(commit);
            }
        }catch (HttpClientErrorException e){
            throw new RuntimeException("项目路径错误或超时，请重试");
        } catch (JsonParseException | JsonMappingException e) {
            throw new RuntimeException("获取信息失败");
        } catch (IOException e) {
            throw new RuntimeException("获取信息失败");
        }catch (ParseException e) {
            throw new RuntimeException("时间格式获取错误");
        }

        return list;
    }

    /**
     * 插入commit数据
     *
     * @param commits
     * @return
     */
    @Override
    public void insertCommit(Set<Commit> commits) {
        for (Commit commit : commits){
            save(commit);
        }
    }

    /**
     * 通过一条开发成员记录删除这个成员在这个项目中的所有提交记录
     *
     * @param id
     * @return
     */
    @Override
    public MessageResult deleteByCid(int id) {

        UpdateWrapper<Commit> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("cid", id);
        boolean remove = remove(updateWrapper);
        if (remove){
            return new MessageResult(remove, "删除成功");
        }else {
            return new MessageResult(remove, "删除失败");
        }

    }

    /**
     * 通过一个项目成员id查找
     *
     * @param cid
     * @return
     */
    @Override

    public List<Commit> findByCid(int cid) {
        QueryWrapper<Commit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cid", cid);
        return commitMapper.selectList(queryWrapper);
    }
}

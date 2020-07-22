/*
*  Copyright 2019-2020 Zheng Jie
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/
package me.zhengjie.gen.service.impl;

import me.zhengjie.gen.domain.Student;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.gen.repository.StudentRepository;
import me.zhengjie.gen.service.StudentService;
import me.zhengjie.gen.service.dto.StudentDto;
import me.zhengjie.gen.service.dto.StudentQueryCriteria;
import me.zhengjie.gen.service.mapstruct.StudentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author sherlock
* @date 2020-07-22
**/
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    @Override
    public Map<String,Object> queryAll(StudentQueryCriteria criteria, Pageable pageable){
        Page<Student> page = studentRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(studentMapper::toDto));
    }

    @Override
    public List<StudentDto> queryAll(StudentQueryCriteria criteria){
        return studentMapper.toDto(studentRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public StudentDto findById(Integer id) {
        Student student = studentRepository.findById(id).orElseGet(Student::new);
        ValidationUtil.isNull(student.getId(),"Student","id",id);
        return studentMapper.toDto(student);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentDto create(Student resources) {
        return studentMapper.toDto(studentRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Student resources) {
        Student student = studentRepository.findById(resources.getId()).orElseGet(Student::new);
        ValidationUtil.isNull( student.getId(),"Student","id",resources.getId());
        student.copy(resources);
        studentRepository.save(student);
    }

    @Override
    public void deleteAll(Integer[] ids) {
        for (Integer id : ids) {
            studentRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<StudentDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (StudentDto student : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("姓名", student.getName());
            map.put("年龄", student.getAge());
            map.put("手机号码", student.getPhone());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
<template>
  <div class="app-container">
      <!-- 查询和其他操作 -->
	    <div class="filter-container" style="margin: 10px 0 10px 0;">
				<el-input clearable class="filter-item" style="width: 200px;" v-model="keyword" placeholder="请输入分类名"></el-input>
	      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFind">查找</el-button>
	      <el-button class="filter-item" type="primary" @click="handleAdd" icon="el-icon-edit">添加分类</el-button>	              
	    </div>

      <el-table :data="tableData"  style="width: 100%">
      
      <el-table-column type="selection"></el-table-column>
      <el-table-column label="序号" width="60">
	      <template slot-scope="scope">
	        <span >{{scope.$index + 1}}</span>
	      </template>
	    </el-table-column>
	    
	    <el-table-column label="分类名" width="100">
	      <template slot-scope="scope">
	        <span>{{ scope.row.sortName }}</span>
	      </template>
	    </el-table-column>
	    
	    <el-table-column label="分类介绍" width="250">
	      <template slot-scope="scope">
	        <span>{{ scope.row.content }}</span>
	      </template>
	    </el-table-column>
	    
	    <el-table-column label="创建时间" width="160">
	      <template slot-scope="scope">
	        <span >{{ scope.row.createTime }}</span>
	      </template>
	    </el-table-column>
	    
	   	<el-table-column label="状态" width="100">
	   	  <template slot-scope="scope">
		   	  <template v-if="scope.row.status == 1">
		        <span>正常</span>
		      </template>
		      <template v-if="scope.row.status == 2">
		        <span>推荐</span>
		      </template>
		      <template v-if="scope.row.status == 0">
		        <span>已删除</span>
		      </template>
	   	  </template>
	    </el-table-column>
	    
	    <el-table-column label="操作" fixed="right" min-width="230"> 
	      <template slot-scope="scope" >
					<el-button @click="handleStick(scope.row)" type="warning" size="small">置顶</el-button>
	      	<el-button @click="handleEdit(scope.row)" type="primary" size="small">编辑</el-button>
	        <el-button @click="handleDelete(scope.row)" type="danger" size="small">删除</el-button>
	      </template>
	    </el-table-column>     	    
	  </el-table>

    <!--分页-->
    <div class="block">
        <el-pagination
          @current-change="handleCurrentChange"
          :current-page.sync="currentPage"
          :page-size="pageSize"
          layout="total, prev, pager, next, jumper"
          :total="total">
        </el-pagination>
    </div>

	  <!-- 添加或修改对话框 -->
		<el-dialog :title="title" :visible.sync="dialogFormVisible">
		  <el-form :model="form">
		  	
		    <el-form-item v-if="isEditForm == true" label="分类UID" :label-width="formLabelWidth">
		      <el-input v-model="form.uid" auto-complete="off" disabled></el-input>
		    </el-form-item>
		    
		   	<el-form-item v-if="isEditForm == false" label="分类UID" :label-width="formLabelWidth" style="display: none;">
		      <el-input v-model="form.uid" auto-complete="off"></el-input>
		    </el-form-item>
		    
        <el-form-item label="分类名" :label-width="formLabelWidth">
		      <el-input v-model="form.sortName" auto-complete="off"></el-input>
		    </el-form-item>
		    
		    <el-form-item label="分类介绍" :label-width="formLabelWidth">
		      <el-input v-model="form.content" auto-complete="off"></el-input>
		    </el-form-item>
		  </el-form>
		  <div slot="footer" class="dialog-footer">
		    <el-button @click="dialogFormVisible = false">取 消</el-button>
		    <el-button type="primary" @click="submitForm">确 定</el-button>
		  </div>
		</el-dialog>

  </div>
</template>

<script>
import {
  getBlogSortList,
  addBlogSort,
  editBlogSort,
  deleteBlogSort,
  stickBlogSort
} from "@/api/blogSort";
import { formatData } from "@/utils/webUtils";
export default {
  data() {
    return {
      tableData: [],
      keyword: "",
      currentPage: 1,
      pageSize: 10,
      total: 0, //总数量
      title: "增加分类",
      dialogFormVisible: false, //控制弹出框
      formLabelWidth: "120px",
      isEditForm: false,
      form: {
        uid: null,
        content: "",
        sortName: ""
      }
    };
  },
  created() {
    this.blogSortList();
  },
  methods: {
    blogSortList: function() {
      var params = new URLSearchParams();
      params.append("keyword", this.keyword);
      params.append("currentPage", this.currentPage);
      params.append("pageSize", this.pageSize);
      getBlogSortList(params).then(response => {
        this.tableData = response.data.records;
        this.currentPage = response.data.current;
        this.pageSize = response.data.size;
        this.total = response.data.total;
      });
    },
    getFormObject: function() {
      var formObject = {
        uid: null,
        content: null,
        sortName: null
      };
      return formObject;
    },
    handleFind: function() {
      this.blogSortList();
    },
    handleAdd: function() {
      this.dialogFormVisible = true;
      this.form = this.getFormObject();
      this.isEditForm = false;
    },
    handleEdit: function(row) {
      this.dialogFormVisible = true;
      this.isEditForm = true;
      console.log(row);
      this.form = row;
    },
    handleStick: function(row) {
      this.$confirm("此操作将会把该标签放到首位, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          let params = new URLSearchParams();
          params.append("uid", row.uid);
          stickBlogSort(params).then(response => {
            if (response.code == "success") {
              this.blogSortList();
              this.$message({
                type: "success",
                message: response.data
              });
            } else {
              this.$message({
                type: "error",
                message: response.data
              });
            }
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消置顶"
          });
        });
    },
    handleDelete: function(row) {
      var that = this;
      this.$confirm("此操作将把分类删除, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          let params = new URLSearchParams();
          params.append("uid", row.uid);
          deleteBlogSort(params).then(response => {
            console.log(response);
            this.$message({
              type: "success",
              message: response.data
            });
            that.blogSortList();
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消删除"
          });
        });
    },
    handleCurrentChange: function(val) {
      console.log("点击了换页");
      this.currentPage = val;
      this.blogSortList();
    },
    submitForm: function() {
      console.log("点击了提交表单", this.form);
      var params = formatData(this.form);
      if (this.isEditForm) {
        editBlogSort(params).then(response => {
          console.log(response);
          if (response.code == "success") {
            this.$message({
              type: "success",
              message: response.data
            });
            this.dialogFormVisible = false;
            this.blogSortList();
          } else {
            this.$message({
              type: "error",
              message: response.data
            });
          }
        });
      } else {
        addBlogSort(params).then(response => {
          console.log(response);
          if (response.code == "success") {
            this.$message({
              type: "success",
              message: response.data
            });
            this.dialogFormVisible = false;
            this.blogSortList();
          } else {
            this.$message({
              type: "error",
              message: response.data
            });
          }
        });
      }
    }
  }
};
</script>
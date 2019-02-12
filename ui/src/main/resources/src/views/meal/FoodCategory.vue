<template>
    <section>
        <el-row>
            <el-col :span="6">
                <el-input placeholder="请输入内容" v-model="searchKey" prefix-icon="el-icon-fa-search">
                    <div slot="append">
                        <el-button type="primary" icon="el-icon-fa-search" @click=" pager.page = 1 ;doSearch()"></el-button>
                    </div>
                </el-input>
            </el-col>
        </el-row>

        <el-table :data="pager.dataList" border stripe style="width: 100%">
            <el-table-column prop="id" label="ID"  header-align="center" align="center" width="55"></el-table-column>
            <el-table-column prop="name" label="名称" show-overflow-tooltip header-align="center" align="center"></el-table-column>
            <el-table-column prop="type" label="类型" show-overflow-tooltip header-align="center" align="center" width="140"></el-table-column>
            <el-table-column prop="seller.name" label="商家" show-overflow-tooltip header-align="center" align="center"></el-table-column>
            <el-table-column prop="onSale" label="状态" show-overflow-tooltip header-align="center" align="center" width="80">
                <template slot-scope="scope">
                    <el-tag :type="scope.row.onSale  ? 'success' : 'danger'" close-transition>{{scope.row.onSale ?'上架' : '已下架'}}</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" show-overflow-tooltip header-align="center" align="center" width="80">
                <template slot-scope="scope">
                    <el-button-group>
                      <el-button title="删除" size="mini" type="primary" icon="el-icon-delete" @click="handleDelete(scope.$index,scope.row)" v-if="scope.row.onSale">删除</el-button>
                      <el-button title="已删除" size="mini" type="danger" icon="el-icon-delete" disabled v-else>已删</el-button>
                    </el-button-group>
                </template>
            </el-table-column>
        </el-table>
        <el-row>
            <el-col :span="6" :offset="18">
                <el-pagination background small style="float:right" layout="prev, pager, next" :total="pager.pager.recordCount" :page-size="pager.pager.pageSize" :current-page.sync="pager.pager.pageNumber" v-show="pager.pager.pageCount != 0" @current-change="changePage"></el-pagination>
            </el-col>
        </el-row>
        <!-- 弹框区域-->
        <el-dialog title="采集商家数据" :visible.sync="addEditShow" width="70%">
            <el-form :model="mcategory" :rules="$rules" ref="mcategoryForm">
                <el-form-item label="地址" :label-width="formLabelWidth" prop="url">
                    <el-input v-model="mcategory.url" auto-complete="off" placeholder="请填写商家地址" suffix-icon="el-icon-fa-vcard" ></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="addEditShow = false ;">取 消</el-button>
                <el-button type="primary" @click="saveMCategory('mcategoryForm')">确 定</el-button>
            </div>
        </el-dialog>
    </section>
</template>
<style>

</style>
<script>
export default {
  data() {
    return {
      searchKey: "",
      pager: {
        pager: {
          pageCount: 0,
          pageNumber: 1,
          pageSize: 15,
          recordCount: 0
        }
      },
      selected: [],
      options: [],
      addEditShow: false,
      grantShow: false,
      mcategory: {
        id: 0,
        name: "",
        description: "",
        avatar: "",
        url:"",
        saleTime:"",
        address:"",
        telephone:""
      },
      formLabelWidth: "120px"
    };
  },
  computed: {
    dialogWidth() {
      return 590 * 100 / this.$utils.windowWidth() + "%";
    }
  },
  watch: {
    options: function() {
      this.selected = [];
      this.options.forEach(item => {
        if (item.selected) {
          this.selected.push(item.key);
        }
      });
    }
  },
  methods: {
    addMCategory() {
      this.mcategory = {
        id: 0,
        url: ""
      };
      this.addEditShow = true;
    },
    target_href(){

    },
    changePage() {
      if (this.searchKey) {
        this.doSearch();
      } else {
        this.loadData();
      }
    },
    doSearch() {
      this.$api.MCategory.search(
        this.pager.pager.pageNumber,
        this.searchKey,
        result => {
          this.pager = result.pager;
        }
      );
    },
    saveMCategory(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          var callback = result => {
            this.changePage();
            this.addEditShow = false;
          };
          this.$api.MCategory.save(this.mcategory, callback);
        } else {
          return false;
        }
      });
    },
    handleEdit(index, row) {
      this.mcategory = row;
      this.addEditShow = true;
    },
    handleDelete(index, row) {
      this.$confirm("确认删除?", "删除确认", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.$api.MCategory.status(row.id, result => {
          this.$message({
            type: "success",
            message: "删除成功!"
          });
          window.setTimeout(() => {
            this.changePage();
          }, 2000);
        });
      });
    },
    loadData() {
      this.$api.MCategory.list(this.pager.pager.pageNumber, 0,result => {
        this.pager = result.pager;
      });
    }
  },
  created: function() {
    this.loadData();
  }
};
</script>
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
            <el-col :span="6" :offset="12">
                <el-button type="primary" icon="el-icon-fa-plus" @click="addSeller" size="small">采集商家</el-button>
            </el-col>
        </el-row>

        <el-table :data="pager.dataList" border stripe style="width: 100%">
            <el-table-column prop="id" label="ID"  header-align="center" align="center" width="55"></el-table-column>
            <el-table-column prop="avatar" label="名称" show-overflow-tooltip header-align="center" align="center" width="120">
              <template slot-scope="scope">
                <img :src="scope.row.avatar" width="100" height="100">
              </template>
            </el-table-column>
            <el-table-column prop="name" label="名称" show-overflow-tooltip header-align="center" align="center" width="240">
              <template slot-scope="scope">
                <a :href="scope.row.url" target="_blank">{{scope.row.name}}</a>
              </template>
            </el-table-column>
            <el-table-column prop="telephone" label="电话" show-overflow-tooltip header-align="center" align="center" width="140"></el-table-column>
            <el-table-column prop="address" label="地址" show-overflow-tooltip header-align="center" align="center"></el-table-column>
             <el-table-column prop="province" label="省份" show-overflow-tooltip header-align="center" align="center"></el-table-column>
            <el-table-column prop="saleTime" label="营业时间" show-overflow-tooltip header-align="center" align="center" width="200"></el-table-column>
            <el-table-column prop="deleted" label="状态" show-overflow-tooltip header-align="center" align="center" width="80">
                <template slot-scope="scope">
                    <el-tag :type="scope.row.deleted  ? 'danger' : 'success'" close-transition>{{scope.row.deleted ?'已下架' : '上架'}}</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" show-overflow-tooltip header-align="center" align="center" width="80">
                <template slot-scope="scope">
                    <el-button-group>
                      <el-button title="删除" size="mini" type="primary" icon="el-icon-delete" @click="handleDelete(scope.$index,scope.row)" v-if="!scope.row.deleted">删除</el-button>
                      <el-button title="已删除" size="mini" type="danger" icon="el-icon-delete" @click="handleDelete(scope.$index,scope.row)" v-else>已删</el-button>
                    </el-button-group>
                </template>
            </el-table-column>
        </el-table>
        <el-row>
            <el-col :span="6" :offset="18">
                <el-pagination background small style="float:right" layout="prev, pager, next"
                               :total="pager.pager.recordCount" :page-size="pager.pager.pageSize"
                               :current-page.sync="pager.pager.pageNumber" v-show="pager.pager.pageCount != 0"
                               @current-change="changePage">
                </el-pagination>
            </el-col>
        </el-row>
        <!-- 弹框区域-->
        <el-dialog title="采集商家数据" :visible.sync="addEditShow" width="70%">
            <el-form :model="seller" :rules="$rules" ref="sellerForm">
                <el-form-item label="地址" :label-width="formLabelWidth" prop="url">
                    <el-input v-model="seller.url" auto-complete="off" placeholder="请填写商家地址" suffix-icon="el-icon-fa-vcard" ></el-input>
                </el-form-item>
                <el-form-item label="地区" :label-width="formLabelWidth" prop="province">
                  <el-radio-group v-model="seller.province">
                    <el-radio label="北京市">北京市</el-radio>
                    <el-radio label="陕西省">陕西省</el-radio>
                  </el-radio-group>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="addEditShow = false ;">取 消</el-button>
                <el-button type="primary" @click="saveSeller('sellerForm')">确 定</el-button>
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
      seller: {
        id: 0,
        name: "",
        description: "",
        avatar: "",
        url:"",
        saleTime:"",
        address:"",
        telephone:"",
        province:"北京市"
      },
      formLabelWidth: "120px"
    };
  },
  computed: {
    dialogWidth() {
      return 890 * 100 / this.$utils.windowWidth() + "%";
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
    addSeller() {
      this.seller = {
        id: 0,
        url: '',
        province:'北京市'
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
      this.$api.Seller.search(
        this.pager.pager.pageNumber,
        this.searchKey,
        result => {
          this.pager = result.pager;
        }
      );
    },
    saveSeller(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          var callback = result => {
            this.changePage();
            this.addEditShow = false;
          };
          console.log(this.seller)
          this.$api.Seller.save(this.seller, callback);
        } else {
          return false;
        }
      });
    },
    handleEdit(index, row) {
      this.seller = row;
      this.addEditShow = true;
    },
    handleDelete(index, row) {
      this.$confirm(row.deleted?"确认恢复?":"确认删除?", row.deleted?"恢复确认":"删除确认", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.$api.Seller.status(row.id, result => {
          this.$message({
            type: "success",
            message: row.deleted?"恢复成功!":"删除成功!"
          });
          window.setTimeout(() => {
            this.changePage();
          }, 2000);
        });
      });
    },
    loadData() {
      this.$api.Seller.list(this.pager.pager.pageNumber, result => {
        this.pager = result.pager;
      });
    }
  },
  created: function() {
    this.loadData();
  }
};
</script>
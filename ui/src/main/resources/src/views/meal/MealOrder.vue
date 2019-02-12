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
          <el-table-column type="expand">
            <template slot-scope="props">
              <el-form label-position="left" inline class="demo-table-expand">
                <div v-for="(item, index) in props.row.info">
                  <el-form-item label="图片"><span><img :src="item.image" width="60" height="60"> </span></el-form-item>
                  <el-form-item label="商品名称"><span>{{ item.name }}</span></el-form-item>
                  <el-form-item label="价格"><span>¥{{ item.price }}</span></el-form-item>
                  <el-form-item label="数量"><span>{{ item.number }}</span></el-form-item>
                  <el-form-item label="店铺地址"><span>{{ item.address }}</span></el-form-item>
                  <el-form-item label="店铺电话"><span>{{ item.telephone }}</span></el-form-item>
                  <el-form-item label="店铺网址"><span><a :href="item.url" target="_blank">{{ item.url }}</a></span></el-form-item>
                </div>
              </el-form>
            </template>
          </el-table-column>
          <el-table-column label="订单ID" prop="id"> </el-table-column>
          <el-table-column label="下单人" prop="name"> </el-table-column>
          <el-table-column label="下单区域" prop="user_province"> </el-table-column>
          <el-table-column label="下单时间" prop="create_time"> </el-table-column>
        </el-table>
        <el-row>
            <el-col :span="6" :offset="18">
                <el-pagination background small style="float:right" layout="prev, pager, next" :total="pager.pager.recordCount" :page-size="pager.pager.pageSize" :current-page.sync="pager.pager.pageNumber" v-show="pager.pager.pageCount != 0" @current-change="changePage"></el-pagination>
            </el-col>
        </el-row>
        <!-- 弹框区域-->
        <el-dialog title="采集商家数据" :visible.sync="addEditShow" width="70%">
            <el-form :model="food" :rules="$rules" ref="foodForm">
                <el-form-item label="地址" :label-width="formLabelWidth" prop="url">
                    <el-input v-model="food.url" auto-complete="off" placeholder="请填写商家地址" suffix-icon="el-icon-fa-vcard" ></el-input>
                </el-form-item>
            </el-form>
        </el-dialog>
    </section>
</template>
<style>
  .demo-table-expand {
    font-size: 0;
  }
  .demo-table-expand label {
    width: 90px;
    color: #99a9bf;
  }
  .demo-table-expand .el-form-item {
    margin-right: 0;
    margin-bottom: 0;
    width: 50%;
  }
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
      food: {
        id: 0,
        name: "",
        info: [],
        create_time: ""
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
    addMFood() {
      this.food = {
        id: 0,
        url: ""
      };
      this.addEditShow = true;
    },
    changePage() {
      if (this.searchKey) {
        this.doSearch();
      } else {
        this.loadData();
      }
    },
    doSearch() {
      this.$api.MOrder.search(
        this.pager.pager.pageNumber,
        this.searchKey,
        result => {
          this.pager = result.pager;
        }
      );
    },
    handleEdit(index, row) {
      this.food = row;
      this.addEditShow = true;
    },
    loadData() {
      this.$api.MOrder.list(this.pager.pager.pageNumber, result => {
        this.pager = result.pager;
      });
    }
  },
  created: function() {
    this.loadData();
  }
};
</script>
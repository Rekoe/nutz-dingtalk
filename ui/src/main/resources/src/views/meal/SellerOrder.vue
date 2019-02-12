<template>
    <section>
        <el-table :data="pager.dataList" border stripe style="width: 100%">
          <el-table-column type="expand">
            <template slot-scope="props">
              <el-form label-position="left" inline class="demo-table-expand">
                <div v-for="(item, index) in props.row.items">
                  <el-form-item label="Id"><span>{{index+1}}</span></el-form-item>
                  <el-form-item label="图片"><span><img :src="item.image" width="60" height="60"> </span></el-form-item>
                  <el-form-item label="商品名称"><span>{{ item.name }}</span></el-form-item>
                  <el-form-item label="统计"><span>¥{{ item.price }} X {{ item.number }}</span></el-form-item> 
                </div>
              </el-form>
            </template>
          </el-table-column>
          <el-table-column label="店铺名称" prop="name" width="180"> </el-table-column>
          <el-table-column label="所在省份" prop="province" width="180"> </el-table-column>
          <el-table-column label="电话" prop="telephone" width="120"> </el-table-column>
          <el-table-column label="地址" prop="address"> </el-table-column>
          <el-table-column label="网址" prop="url"> 
            <template slot-scope="scope">
                <a :href="scope.row.url" target="_blank">{{scope.row.url}}</a>
              </template>
          </el-table-column>
        </el-table>
        <el-row>
            <el-col :span="6" :offset="18">
                <el-pagination background small style="float:right" layout="prev, pager, next" :total="pager.pager.recordCount" :page-size="pager.pager.pageSize" :current-page.sync="pager.pager.pageNumber" v-show="pager.pager.pageCount != 0" @current-change="changePage"></el-pagination>
            </el-col>
        </el-row>
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
    width: 25%;
    color: #010b1a;
  }
</style>
<script>
export default {
  data() {
    return {
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
    changePage() {
      this.loadData();
    },
    handleEdit(index, row) {
      this.food = row;
      this.addEditShow = true;
    },
    loadData() {
      this.$api.MSellerOrder.list(this.pager.pager.pageNumber, result => {
        this.pager = result.pager;
      });
    }
  },
  created: function() {
    this.loadData();
  }
};
</script>
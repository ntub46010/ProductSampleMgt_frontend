# ProductSampleMgt_frontend
大學在研究中心開發的Android App，主題為「樣品管理系統」。<br>
用途為管理磁磚產品，亦有購物車及建立訂單方面的功能。

## 登入畫面
主程式：<a href="https://github.com/ntub46010/ProductSampleMgt_frontend/blob/master/app/src/main/java/com/vincent/psm/LoginActivity.java">LoginActivity</a><br>
說明：輸入帳密才可登入。清除Token的功能是，在重新安裝App或更換裝置時，可能會出現Firebase辨識的問題，造成無法登入。此只要進行清除，再嘗試登入即可。<br>
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E7%99%BB%E5%85%A5%E7%95%AB%E9%9D%A2.png" height="24%" width="24%" />

## 首頁
主程式：<a href="https://github.com/ntub46010/ProductSampleMgt_frontend/blob/master/app/src/main/java/com/vincent/psm/MainActivity.java">MainActivity</a>
<br>
說明：呈現本App主要功能，可點選進入，惟部份功能僅限特定身份使用。業務可使用購物車。倉管人員可查看訂單。產品管理員可進行產品的新增、修改、下架等管理。<br>
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E4%B8%BB%E5%8A%9F%E8%83%BD%E7%95%AB%E9%9D%A2.png" height="24%" width="24%" />

## 瀏覽產品
主程式：<a href="https://github.com/ntub46010/ProductSampleMgt_frontend/blob/master/app/src/main/java/com/vincent/psm/product/ProductHomeActivity.java">ProductHomeActivity</a>
<br>
說明：查看所有上架中的產品，顯示其圖片、產品名稱、長寬厚度及價格。點選右下方的箭頭按鈕可回到清單上方，而放大鏡圖案是搜尋功能。<br>
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E7%94%A2%E5%93%81%E9%A6%96%E9%A0%81.png" height="24%" width="24%" />

## 搜尋產品
主程式：<a href="https://github.com/ntub46010/ProductSampleMgt_frontend/blob/master/app/src/main/java/com/vincent/psm/product/ProductSearchActivity.java">ProductSearchActivity</a>
<br>
說明：透過編號、名稱、材質、顏色四個條件其中一項，來搜尋產品。也可指定要將下架中的產品一併列出來。
<table><tr><td>
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E6%90%9C%E5%B0%8B1.png" height="24%" width="24%" />
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E6%90%9C%E5%B0%8B2.png" height="24%" width="24%" />
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E6%90%9C%E5%B0%8B3.png" height="24%" width="24%" />
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E6%90%9C%E5%B0%8B4.png" height="24%" width="24%" />
</td></tr></table>

## 商品詳情
主程式：<a href="https://github.com/ntub46010/ProductSampleMgt_frontend/blob/master/app/src/main/java/com/vincent/psm/product/ProductDetailActivity.java">ProductDetailActivity</a><br>
說明：顯示產品詳細資料，可放大圖片。右下方按鈕，業務可加入購物車；倉管可追加至訂單；管理員可編輯產品。
<table><tr><td>
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E5%95%86%E5%93%81%E8%A9%B3%E6%83%851.png" height="24%" width="24%" />
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E5%95%86%E5%93%81%E8%A9%B3%E6%83%852.png" height="24%" width="24%" />
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E5%95%86%E5%93%81%E8%A9%B3%E6%83%853.png" height="24%" width="24%" />
</td></tr></table>

## 刊登產品
主程式：<a href="https://github.com/ntub46010/ProductSampleMgt_frontend/blob/master/app/src/main/java/com/vincent/psm/product/ProductPostActivity.java">ProductPostActivity</a><br>
說明：填寫品名、材質、顏色、價格等資料。並從手機圖庫中選取照片，依固定長寬比例裁切。
<table><tr><td>
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E5%88%8A%E7%99%BB%E7%94%A2%E5%93%81.png" height="24%" width="24%" />
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E8%A3%81%E5%89%AA%E5%9C%96%E7%89%87.png" height="24%" width="24%" />
</td></tr></table>

## 產品管理
### 首頁
主程式：<a href="https://github.com/ntub46010/ProductSampleMgt_frontend/blob/master/app/src/main/java/com/vincent/psm/product/ProductMgtActivity.java">ProductMgtActivity</a><br>
說明：列出所有上架與下架的產品。下架中的產品，底色為灰色。右下方數字分別是庫存與安全庫存，且若庫存低於安全存量，會以紅字顯示。<br>
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E5%95%86%E5%93%81%E7%AE%A1%E7%90%861.png" height="24%" width="24%" />

### 編輯
主程式：<a href="https://github.com/ntub46010/ProductSampleMgt_frontend/blob/master/app/src/main/java/com/vincent/psm/product/ProductUpdateActivity.java">ProductUpdateActivity</a><br>
說明：編輯產品詳細資料。包括：一般編輯、編輯後上架與下架三種。也可變更產品圖片。
<table><tr><td>
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E7%B7%A8%E8%BC%AF%E7%94%A2%E5%93%811.png" height="24%" width="24%" />
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E7%B7%A8%E8%BC%AF%E7%94%A2%E5%93%812.png" height="24%" width="24%" />
</td></tr></table>

## 購物車首頁
主程式：<a href="https://github.com/ntub46010/ProductSampleMgt_frontend/blob/master/app/src/main/java/com/vincent/psm/cart/CartHomeActivity.java">CartHomeActivity</a><br>
說明：購物車相關功能限業務使用。在此可查看自己的購物車清單。新增購物車後可加入產品，進而建立訂單。長按時會設定為預設購物車，以便在商品詳情頁面直接加入產品。
<table><tr><td>
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E8%B3%BC%E7%89%A9%E8%BB%8A1.png" height="24%" width="24%" />
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E8%B3%BC%E7%89%A9%E8%BB%8A2.png" height="24%" width="24%" />
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E8%B3%BC%E7%89%A9%E8%BB%8A5.png" height="24%" width="24%" />
</td></tr></table>

## 購物車明細
主程式：<a href="https://github.com/ntub46010/ProductSampleMgt_frontend/blob/master/app/src/main/java/com/vincent/psm/cart/CartDetailActivity.java">CartDetailActivity</a><br>
說明：查看車內現有產品，包含數量與小計。右下方按鈕分別用以顯示購物車摘要及新增產品至車內。點擊藍色的聯絡電話可進行撥打。點擊右上方則是建立訂單。
<table><tr><td>
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E8%B3%BC%E7%89%A9%E8%BB%8A4.png" height="24%" width="24%" />
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E8%B3%BC%E7%89%A9%E8%BB%8A3.png" height="24%" width="24%" />
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E8%B3%BC%E7%89%A9%E8%BB%8A6.png" height="24%" width="24%" />
</td></tr></table>

## 建立訂單
主程式：<a href="https://github.com/ntub46010/ProductSampleMgt_frontend/blob/master/app/src/main/java/com/vincent/psm/order/OrderCreateActivity.java">OrderCreateActivity</a><br>
說明：將購物車轉換為訂單。若資料庫中有該客戶資料，可用自動完成方式輸入名稱，藉此帶入電話與地址的欄位資料。自動完成客戶名稱後，聯絡人的部份即可從清單中選擇一位，若不存在亦可新增一位。另外，假設提交原先未有的客戶或聯絡人，系統會將新增至資料庫。
<table><tr><td>
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E5%BB%BA%E7%AB%8B%E8%A8%82%E5%96%AE1.png" height="24%" width="24%" />
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E5%BB%BA%E7%AB%8B%E8%A8%82%E5%96%AE2.png" height="24%" width="24%" />
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E5%BB%BA%E7%AB%8B%E8%A8%82%E5%96%AE3.png" height="24%" width="24%" />
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E5%BB%BA%E7%AB%8B%E8%A8%82%E5%96%AE4.png" height="24%" width="24%" />
</td></tr></table>



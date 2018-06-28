# ProductSampleMgt_frontend
大學在研究中心開發的Android App，主題為「樣品管理系統」。<br>
用途為管理磁磚產品，亦有購物車、訂單、通知的功能。並有使用者身份的特色，不同身份能使用的功能會有些不同。

## 登入畫面
主程式：<a href="https://github.com/ntub46010/ProductSampleMgt_frontend/blob/master/app/src/main/java/com/vincent/psm/LoginActivity.java">LoginActivity</a><br>
說明：輸入帳密才可登入。清除Token的功能是，在重新安裝App或更換裝置時，可能會出現Firebase存取Token的問題，造成無法登入。此時只要進行清除，再嘗試登入即可。<br>
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E7%99%BB%E5%85%A5%E7%95%AB%E9%9D%A2.png" height="24%" width="24%" />

## 首頁
主程式：<a href="https://github.com/ntub46010/ProductSampleMgt_frontend/blob/master/app/src/main/java/com/vincent/psm/MainActivity.java">MainActivity</a>
<br>
說明：呈現本App主要功能，可點選進入，惟部份功能僅限特定身份使用。業務可操作購物車。業務、倉管人員可使用訂單(但業務僅能查看)。產品管理員可進行產品的新增、修改、下架等管理。<br>
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

## 產品詳情
主程式：<a href="https://github.com/ntub46010/ProductSampleMgt_frontend/blob/master/app/src/main/java/com/vincent/psm/product/ProductDetailActivity.java">ProductDetailActivity</a><br>
說明：顯示產品詳細資料，可放大圖片。右下方按鈕，業務可加入購物車；倉管可追加至訂單；管理員可編輯產品。
<table><tr><td>
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E5%95%86%E5%93%81%E8%A9%B3%E6%83%851.png" height="24%" width="24%" />
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E5%95%86%E5%93%81%E8%A9%B3%E6%83%852.png" height="24%" width="24%" />
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E5%95%86%E5%93%81%E8%A9%B3%E6%83%853.png" height="24%" width="24%" />
</td></tr></table>

## 刊登產品
主程式：<a href="https://github.com/ntub46010/ProductSampleMgt_frontend/blob/master/app/src/main/java/com/vincent/psm/product/ProductPostActivity.java">ProductPostActivity</a><br>
說明：填寫品名、材質、顏色、價格等資料。並從手機圖庫中選取照片，依固定長寬比例裁切。確定後上傳。
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
說明：查看車內現有產品，包含數量、庫存量與小計。右下方按鈕分別用以顯示購物車摘要及新增產品至車內。在摘要視窗點擊藍色的聯絡電話可進行撥打。點擊右上方則是建立訂單。
<table><tr><td>
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E8%B3%BC%E7%89%A9%E8%BB%8A4.png" height="24%" width="24%" />
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E8%B3%BC%E7%89%A9%E8%BB%8A3.png" height="24%" width="24%" />
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E8%B3%BC%E7%89%A9%E8%BB%8A6.png" height="24%" width="24%" />
</td></tr></table>

## 建立訂單
主程式：<a href="https://github.com/ntub46010/ProductSampleMgt_frontend/blob/master/app/src/main/java/com/vincent/psm/order/OrderCreateActivity.java">OrderCreateActivity</a><br>
說明：將購物車轉換為訂單，並且此操作會刪除原購物車。若資料庫中有該客戶資料，可用自動完成方式輸入名稱，藉此帶入電話與地址的欄位資料。自動完成客戶名稱後，聯絡人的部份即可從清單中選擇一位，亦可新增一位。另外，假設提交原先未有的客戶或聯絡人，系統將會新增至資料庫。訂單建立期間會先檢查庫存量，避免加進購物車後，日後庫存不夠，卻仍可建立訂單。最後，倉管人員與產品管理員會收到相關的推播。
<table>
<tr><td>
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E5%BB%BA%E7%AB%8B%E8%A8%82%E5%96%AE2.png" height="24%" width="24%" />
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E5%BB%BA%E7%AB%8B%E8%A8%82%E5%96%AE3.png" height="24%" width="24%" />
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E5%BB%BA%E7%AB%8B%E8%A8%82%E5%96%AE4.png" height="24%" width="24%" />
</td></tr>
<tr><td>
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E6%8E%A8%E6%92%AD1.png" height="24%" width="24%" />
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E6%8E%A8%E6%92%AD3.png" height="24%" width="24%" />
</tr></td>
</table>

## 訂單首頁
主程式：<a href="https://github.com/ntub46010/ProductSampleMgt_frontend/blob/master/app/src/main/java/com/vincent/psm/order/OrderHomeActivity.java">OrderHomeActivity</a><br>
說明：檢視現有訂單，包括編號、公司名稱、預計到貨日、訂單金額及訂單處理狀態。依照處理狀態排序與到貨日排序。
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E8%A8%82%E5%96%AE%E9%A6%96%E9%A0%81.png" height="24%" width="24%" />

## 訂單詳情
主程式：<a href="https://github.com/ntub46010/ProductSampleMgt_frontend/blob/master/app/src/main/java/com/vincent/psm/order/OrderDetailActivity.java">OrderDetailActivity</a><br>
說明：查看訂單資料與產品明細。如客戶聯絡方式(點按可撥打電話)、其他訂單訊息與負責業務(點按可查看該人資料)。而產品明細包含編號、名稱、規格、數量與小計。將訂單資料往上滑，可將產品明細的清單完全露出。而從Toolbar往下滑則可收回清單。右下方的按鈕僅限倉管身份使用，可追加產品或編輯訂單資料。
<table><tr><td>
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E8%A8%82%E5%96%AE%E8%A9%B3%E6%83%851.png" height="24%" width="24%" />
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E8%A8%82%E5%96%AE%E8%A9%B3%E6%83%852.png" height="24%" width="24%" />
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E5%80%8B%E4%BA%BA%E6%AA%94%E6%A1%881.png" height="24%" width="24%" />
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E8%BF%BD%E5%8A%A0%E8%A8%82%E5%96%AE%E7%94%A2%E5%93%81.png" height="24%" width="24%" />
</td></tr></table>

## 編輯訂單
主程式：<a href="https://github.com/ntub46010/ProductSampleMgt_frontend/blob/master/app/src/main/java/com/vincent/psm/order/OrderUpdateActivity.java">OrderUpdateActivity</a><br>
說明：在此能編輯除負責業務之外的所有訂單資料，例如更改預計到貨日。第一張圖片意味著，資料庫已有該聯絡人資料，往後可直接從清單選取。至於上方的訂單狀態選項，可讓倉管人員視情況更新，並發送至業務的手機。
<table><tr><td>
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E7%B7%A8%E8%BC%AF%E8%A8%82%E5%96%AE2.png" height="24%" width="24%" />
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E7%B7%A8%E8%BC%AF%E8%A8%82%E5%96%AE1.png" height="24%" width="24%" />
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E6%8E%A8%E6%92%AD2.png" height="24%" width="24%" />
</td></tr></table>

## 通知
主程式：<a href="https://github.com/ntub46010/ProductSampleMgt_frontend/blob/master/app/src/main/java/com/vincent/psm/notification/NotificationActivity.java">NotificationActivity</a><br>
說明：上述有些程式操作，如建立訂單、追加訂單產品等，都會引發推播。但除了推播，本程式也有通知列表，供使用者瀏覽先前發生的事件，點擊項目可詳細檢視。建立訂單會發送新訂單通知(倉管人員)與庫存不足通知(產品管理員)。訂單動態更新會發送訂單處理狀態給業務。追加訂單產品會發送庫存不足通知給產品管理員。
<table><tr><td>
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E9%80%9A%E7%9F%A51.png" height="24%" width="24%" />
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E9%80%9A%E7%9F%A52.png" height="24%" width="24%" />
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E9%80%9A%E7%9F%A53.png" height="24%" width="24%" />
</td></tr></table>

## 個人檔案
主程式：<a href="https://github.com/ntub46010/ProductSampleMgt_frontend/blob/master/app/src/main/java/com/vincent/psm/profile/ProfileActivity.java">ProfileActivity</a><br>
說明：顯示使用者的個人檔案，且文字可長按進行複製。點擊右上方按鈕會進入編輯畫面。若是從訂單詳情的負責業務來到此畫面，則電話與電子郵件會變為藍色，並且點擊後能撥打電話或開啟電子郵件軟體。
<table><tr><td>
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E5%80%8B%E4%BA%BA%E6%AA%94%E6%A1%882.png" height="24%" width="24%" />
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E5%80%8B%E4%BA%BA%E6%AA%94%E6%A1%883.png" height="24%" width="24%" />
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E5%80%8B%E4%BA%BA%E6%AA%94%E6%A1%884.png" height="24%" width="24%" />
</td></tr></table>

## 編輯個人檔案
主程式：<a href="https://github.com/ntub46010/ProductSampleMgt_frontend/blob/master/app/src/main/java/com/vincent/psm/profile/ProfileUpdateActivity.java">ProfileUpdateActivity</a><br>
說明：編輯個人檔案，包括姓名、電話、電子郵件與密碼。密碼會經由MD5雜湊函數處理，儲存於資料庫。
<table><tr><td>
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E5%80%8B%E4%BA%BA%E6%AA%94%E6%A1%88%E4%BF%AE%E6%94%B91.png" height="24%" width="24%" />
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E5%80%8B%E4%BA%BA%E6%AA%94%E6%A1%88%E4%BF%AE%E6%94%B92.png" height="24%" width="24%" />
<img src="https://github.com/ntub46010/Photos/blob/master/ProductSampleMgt%E6%93%8D%E4%BD%9C%E7%95%AB%E9%9D%A2/%E5%80%8B%E4%BA%BA%E6%AA%94%E6%A1%88%E4%BF%AE%E6%94%B93.png" height="24%" width="24%" />
</td></tr></table>

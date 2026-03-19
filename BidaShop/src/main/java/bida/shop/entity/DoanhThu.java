package bida.shop.entity;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DoanhThu {
    private String id;
    private Date ngayBaoCao;
    private double doanhThuBan;
    private double doanhThuDoUong;
    private double tongDoanhThu;
}